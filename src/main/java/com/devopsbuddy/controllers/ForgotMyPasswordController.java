package com.devopsbuddy.controllers;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.I18NService;
import com.devopsbuddy.backend.service.PasswordResetTokenService;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.utils.UserUtils;

@Controller
public class ForgotMyPasswordController {

    private static final Logger       LOG                              = LoggerFactory.getLogger(ForgotMyPasswordController.class);

    public static final String        EMAIL_ADDRESS_VIEW_NAME          = "forgotmypassword/emailForm";
    public static final String        FORGOT_PASSWORD_URL_MAPPING      = "/forgotmypassword";
    public static final String        CHANGE_PASSWORD_PATH             = "/changeuserpassword";
    public static final String        CHANGE_PASSWORD_VIEW_NAME        = "forgotmypassword/changePassword";
    public static final String        EMAIL_SENT_KEY                   = "mailSent";
    public static final String        EMAIl_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";

    private static final String       PASSWORD_RESET_ATTRIBUTE_NAME    = "passwordReset";

    private static final String       MESSAGE_ATTRIBUTE_NAME           = "message";

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private I18NService               i18nService;

    @Autowired
    private EmailService              emailService;

    @Autowired
    private UserService               userService;

    @Value("${webmaster.email}")
    private String                    webMasterEmail;

    @RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.GET)
    public String forgotPasswordGet() {
        return EMAIL_ADDRESS_VIEW_NAME;
    }

    @RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.POST)
    public String forgotPasswordPost(HttpServletRequest request, @RequestParam("email") String email, ModelMap modelMap) {
        PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetTokenForEmail(email);
        if (null == passwordResetToken) {
            LOG.warn("could not find password reset token for this email: {}", email);
        } else {
            User user = passwordResetToken.getUser();
            String token = passwordResetToken.getToken();
            String passwordResetUrl = UserUtils.createPasswordResetUrl(request, user.getId(), token);
            LOG.debug("Reset Password URL:{}", passwordResetUrl);

            String emailText = i18nService.getMessageSource(EMAIl_MESSAGE_TEXT_PROPERTY_NAME, request.getLocale());

            LOG.debug("Creating mail template for user:{}", user.getUsername());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("[Devopsbuddy]: How to reset your password ");
            message.setText(emailText + "\r\n" + passwordResetUrl);
            message.setFrom(webMasterEmail);
            emailService.sendGenericEmailMessage(message);
        }
        modelMap.addAttribute(EMAIL_SENT_KEY, true);
        return EMAIL_ADDRESS_VIEW_NAME;
    }

    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.GET)
    public String changeUserPasswordGet(@RequestParam("id") long id, @RequestParam("token") String token, Locale locale, ModelMap model) {

        if (StringUtils.isEmpty(token) || id == 0) {
            LOG.error("Invalid token value {} or user ID {} ", token, id);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Invalid user id or token value");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);

        if (null == passwordResetToken) {
            LOG.warn("A token could not be found with value {}", token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Token Not Found");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        User user = passwordResetToken.getUser();

        if (user.getId() != id) {
            LOG.error("the user id {} passed as parameter does not match the user id {} associated with token {}", id, user.getId(), token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, i18nService.getMessageSource("resetPassword.token.invalid", locale));
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        if (LocalDateTime.now(Clock.systemUTC()).isAfter(passwordResetToken.getExpiryDate())) {
            LOG.error("The token {} has expired", token);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, i18nService.getMessageSource("resetPassword.token.expired", locale));
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        model.addAttribute("principalId", user.getId());

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return CHANGE_PASSWORD_VIEW_NAME;
    }

    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.POST)
    public String changePasswordPost(@RequestParam("principal_id") long userId, @RequestParam("password") String password, ModelMap model)
            throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (null == auth) {
            LOG.error("An unauthenticated user tried to invoke the reset password POST method");
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "You are not authorized to perform this request");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        User user = (User) auth.getPrincipal();
        if (user.getId() != userId) {
            LOG.error("Security Breach!! User {} is trying to make the password reset on behalf of {}", user.getId(), userId);
            model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "You are not authorized to perform this request");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        userService.updateUserPassword(userId, password);
        LOG.info("Password successfully updated for user {}", user.getUsername());
        model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "true");
        return CHANGE_PASSWORD_VIEW_NAME;
    }

}
