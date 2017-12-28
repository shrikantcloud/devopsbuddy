package com.devopsbuddy.utils;

import com.devopsbuddy.backend.persistence.domain.backend.User;

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

}
