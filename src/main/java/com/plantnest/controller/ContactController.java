package com.plantnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

   
    @GetMapping("/contact")
    public String showContactPage() {
        return "contact"; // This will resolve to src/main/resources/templates/contact.html
    }

    /**
     * Handles POST requests to the /contact URL when the form is submitted.
     * This is a placeholder for actual contact form submission logic (e.g., sending an email).
     *
     * @param name The name entered in the form.
     * @param email The email entered in the form.
     * @param subject The subject entered in the form.
     * @param message The message entered in the form.
     * @param redirectAttributes Used to add flash attributes for success/error messages after redirect.
     * @return A redirect to the contact page with a success or error parameter.
     */
    @PostMapping("/contact")
    public String submitContactForm(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("subject") String subject,
            @RequestParam("message") String message,
            RedirectAttributes redirectAttributes) {


        System.out.println("Received contact form submission:");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);


        redirectAttributes.addFlashAttribute("success", true);

        return "redirect:/contact";
    }
}
