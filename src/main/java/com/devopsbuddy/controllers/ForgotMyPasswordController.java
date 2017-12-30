package com.devopsbuddy.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.I18NService;
import com.devopsbuddy.backend.service.PasswordResetTokenService;
import com.devopsbuddy.utils.UserUtils;

@Controller
public class ForgotMyPasswordController {
    
    private static final Logger LOG = LoggerFactory.getLogger(ForgotMyPasswordController.class);
    
    public static final String EMAIL_ADDRESS_VIEW_NAME = "forgotmypassword/emailForm";
    public static final String FORGOT_PASSWORD_URL_MAPPING = "/forgotmypassword";
    public static final String CHANGE_PASSWORD_PATH = "/changeuserpassword";
    public static final String EMAIL_SENT_KEY = "mailSent";
    public static final String EMAIl_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";
    
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;
    
    @Autowired
    private I18NService i18nService;
    
    @Autowired
    private EmailService emailService;
    
    @Value("${webmaster.email}")
    private String webMasterEmail;
    
    @RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.GET)
    public String forgotPasswordGet() {
        return EMAIL_ADDRESS_VIEW_NAME;
    }
    
    @RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.POST)
    public String forgotPasswordPost(HttpServletRequest request, @RequestParam ("email") String email, ModelMap modelMap) {
        PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetTokenForEmail(email);
        if (null == passwordResetToken) {
            LOG.warn("could not find password reset token for this email: {}",email);
        } else {
            User user = passwordResetToken.getUser();
            String token = passwordResetToken.getToken();
            String passwordResetUrl = UserUtils.createPasswordResetUrl(request, user.getId(), token);
            LOG.debug("Reset Password URL:{}",passwordResetUrl);
            
            String emailText = i18nService.getMessageSource(EMAIl_MESSAGE_TEXT_PROPERTY_NAME, request.getLocale());
            
            LOG.debug("Creating mail template for user:{}",user.getUsername());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("\"[Devopsbuddy]: How to reset your password \"");
            message.setText(emailText + "\r\n"+ passwordResetUrl);
            message.setFrom(webMasterEmail);
            emailService.sendGenericEmailMessage(message);
        }
        modelMap.addAttribute(EMAIL_SENT_KEY, true);
        return EMAIL_ADDRESS_VIEW_NAME;
    }
    

}
