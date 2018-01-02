package com.devopsbuddy.config;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.MockEmailService;

@Configuration
@Profile("dev")
@PropertySource("classpath:application-dev.properties")
@PropertySource("file:F:/.devopsbuddy/stripe.properties")
public class DevelopmentConfig {

    private static final String H2_CONSOLE_URL = "/console/*";
    
    @Value("${stripe.test.private.key}")
    private String stripeDevKey;

    @Bean
    public EmailService emailService() {
        return new MockEmailService();
    }

    @Bean
    public ServletRegistrationBean h2ConsoleServletRegistration() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new WebServlet());
        bean.addUrlMappings(H2_CONSOLE_URL);
        return bean;
    }
    
    @Bean
    public String stripeKey() {
        return stripeDevKey;
    }

}
