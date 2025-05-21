package com.plantnest.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    public void sendResetLink(String email) {
        // TODO: Add your logic to send reset password email here

        System.out.println("Sending password reset link to: " + email);
    }
}
