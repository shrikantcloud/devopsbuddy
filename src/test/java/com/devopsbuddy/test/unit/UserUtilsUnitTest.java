package com.devopsbuddy.test.unit;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.devopsbuddy.controllers.ForgotMyPasswordController;
import com.devopsbuddy.utils.UserUtils;

import junit.framework.Assert;

@SuppressWarnings("deprecation")
public class UserUtilsUnitTest {

    private static final String    APP_ROOT_URL = "http://localhost:8080";
    private MockHttpServletRequest mockHttpServletRequest;

    @Before
    public void init() {
        mockHttpServletRequest = new MockHttpServletRequest();
    }

    @Test
    public void testPasswordResetEmailUrlConstruction() throws Exception {
        mockHttpServletRequest.setServerPort(8080);
        String token = UUID.randomUUID().toString();
        long userId = 123456;

        String expectedURL = APP_ROOT_URL + ForgotMyPasswordController.CHANGE_PASSWORD_PATH + "?id=" + userId + "&token=" + token;
        String actualURL = UserUtils.createPasswordResetUrl(mockHttpServletRequest, userId, token);
        Assert.assertEquals(expectedURL, actualURL);
    }

}
