package com.plantnest.config; // <- IMPORTANT: Confirm this matches your folder: com/plantnest/config

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect; // <- CRITICAL IMPORT

@Configuration
public class ThymeleafConfig {

    // Ensures proper URL encoding for static resources (important for Spring Security)
    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    // This bean helps the application locate the default servlet for serving static content.
    // It resolved a previous 'BeanCreationException'.
    @Bean
    public DefaultServletHttpRequestHandler defaultServletHttpRequestHandler() {
        DefaultServletHttpRequestHandler handler = new DefaultServletHttpRequestHandler();
        handler.setDefaultServletName("default"); // Typically "default" for embedded Tomcat
        return handler;
    }

    // Configures the Thymeleaf template engine.
    // This is where the SpringSecurityDialect is added to enable #request, #authentication, etc.
    @Bean
    public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        // *** THIS IS THE CRUCIAL LINE: Add the SpringSecurityDialect ***
        templateEngine.addDialect(new SpringSecurityDialect());
        return templateEngine;
    }

}