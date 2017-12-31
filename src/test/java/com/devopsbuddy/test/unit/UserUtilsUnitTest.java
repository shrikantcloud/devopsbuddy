package com.devopsbuddy.test.unit;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.controllers.ForgotMyPasswordController;
import com.devopsbuddy.utils.UserUtils;
import com.devopsbuddy.web.domain.fontend.BasicAccountPayload;

import junit.framework.Assert;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SuppressWarnings("deprecation")
public class UserUtilsUnitTest {

    private static final String    APP_ROOT_URL = "http://localhost:8080";
    private MockHttpServletRequest mockHttpServletRequest;
    private PodamFactory           podamFactory;

    @Before
    public void init() {
        mockHttpServletRequest = new MockHttpServletRequest();
        podamFactory = new PodamFactoryImpl();
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

    @Test
    public void mapWebUserToDomainUser() {
        BasicAccountPayload webUser = podamFactory.manufacturePojoWithFullData(BasicAccountPayload.class);
        webUser.setEmail("me@example.com");

        User user = UserUtils.fromWebUserToDomainUser(webUser);
        Assert.assertNotNull(user);

        Assert.assertNotNull(webUser.getUsername(), user.getUsername());
        Assert.assertNotNull(webUser.getPassword(), user.getPassword());
        Assert.assertNotNull(webUser.getFirstName(), user.getFirstName());
        Assert.assertNotNull(webUser.getLastName(), user.getLastName());
        Assert.assertNotNull(webUser.getEmail(), user.getEmail());
        Assert.assertNotNull(webUser.getPhoneNumber(), user.getPhoneNumber());
        Assert.assertNotNull(webUser.getCountry(), user.getCountry());
        Assert.assertNotNull(webUser.getDescription(), user.getDescription());
    }

}
