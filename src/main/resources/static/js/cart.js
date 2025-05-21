document.addEventListener('DOMContentLoaded', () => {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

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
                    [csrfHeader]: csrfToken
                },
                body: `itemId=${itemId}&quantity=${quantity}`
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    input.value = quantity;
                    document.getElementById('cart-count').textContent = data.cartCount;
                    document.getElementById('total-price').textContent = data.totalPrice.toFixed(2);
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
                headers: { [csrfHeader]: csrfToken }
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
