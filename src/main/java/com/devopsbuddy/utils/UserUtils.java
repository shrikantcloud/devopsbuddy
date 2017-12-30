package com.devopsbuddy.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mock.web.MockHttpServletRequest;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.controllers.ForgotMyPasswordController;

public class UserUtils {

    private UserUtils() throws Exception {
        throw new AssertionError("Non Instantiable");
    }

    public static User createBasicUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("secret");
        user.setEmail(email);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhoneNumber("123456789123");
        user.setCountry("IN");
        user.setEnabled(true);
        user.setDescription("A basic User");
        user.setProfileImageUrl("/images/sa.jpg");
        return user;
    }

    public static String createPasswordResetUrl(HttpServletRequest request, long userId, String token) {
        String passwordResetUrl = 
                request.getScheme() +
                "://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath() +
                ForgotMyPasswordController.CHANGE_PASSWORD_PATH +
                "?id=" +
                userId +
                "&token=" + 
                token;
                
        return passwordResetUrl;
    }

}
