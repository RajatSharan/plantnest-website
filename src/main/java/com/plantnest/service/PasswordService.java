package com.plantnest.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    public void sendResetLink(String email) {

        System.out.println("Sending password reset link to: " + email);
    }
}
