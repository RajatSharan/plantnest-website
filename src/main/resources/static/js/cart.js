document.addEventListener('DOMContentLoaded', () => {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    // Buy Now / Add to Cart
    document.querySelectorAll('.add-to-cart-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const productId = btn.getAttribute('data-id');
            fetch(`/add-to-cart/${productId}`, {
                method: 'POST',
                headers: csrfHeader && csrfToken ? { [csrfHeader]: csrfToken } : {}
            })
            .then(res => res.json())
            .then(data => {
                if (data.cartCount !== undefined) {
                    const cartCountElem = document.getElementById('cart-count');
                    if (cartCountElem) cartCountElem.textContent = data.cartCount;
                }
            });
        });
    });

    // Quantity change
    document.querySelectorAll('.quantity-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemId = btn.getAttribute('data-item-id');
            const input = document.querySelector(`input[data-item-id='${itemId}']`);
            let quantity = parseInt(input.value);

            if (btn.classList.contains('increase')) quantity++;
            else if (btn.classList.contains('decrease') && quantity > 1) quantity--;

            fetch('/cart/update-quantity', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    ...(csrfHeader && csrfToken ? { [csrfHeader]: csrfToken } : {})
                },
                body: `itemId=${itemId}&quantity=${quantity}`
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    input.value = quantity;
                    const cartCountElem = document.getElementById('cart-count');
                    if (cartCountElem && data.cartCount !== undefined) cartCountElem.textContent = data.cartCount;
                    if (data.totalPrice !== undefined) {
                        const totalElem = document.getElementById('total-price');
                        if (totalElem) totalElem.textContent = data.totalPrice.toFixed(2);
                    }
                    location.reload(); // Optionally replace with DOM update
                }
            });
        });
    });

    // Remove item
    document.querySelectorAll('.remove-item-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemId = btn.getAttribute('data-item-id');
            fetch(`/cart/remove/${itemId}`, {
                method: 'POST',
                headers: csrfHeader && csrfToken ? { [csrfHeader]: csrfToken } : {}
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    location.reload();
                }
            });
        });
    });
});