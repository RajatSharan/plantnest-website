package com.plantnest.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addRequestURI(HttpServletRequest request, Model model) {
        model.addAttribute("requestURI", request.getRequestURI());
    }
}
