package com.devopsbuddy.test.integraion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.service.StripeService;
import com.devopsbuddy.enums.PlansEnum;
import com.stripe.Stripe;

import junit.framework.Assert;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class StripeIntegrationTest {

    public static final String TEST_CC_NUMBER       = "4242424242424242";

    public static final int    TEST_CC_EXPIRY_MONTH = 1;

    public static final int    TEST_CC_CVC_NUMBER   = 2019;

    @Autowired
    private StripeService       stripeService;

    @Autowired
    private String              stripeKey;

    @Before
    public void init() {
        Assert.assertNotNull(stripeKey);
        Stripe.apiKey = stripeKey;
    }

    @Test
    public void createStripeCustomer() throws Exception {
        Map<String, Object> tokenParams = new HashMap<>();
        Map<String, Object> cardParams = new HashMap<>();

        cardParams.put("number", TEST_CC_NUMBER);
        cardParams.put("exp_month", TEST_CC_EXPIRY_MONTH);
        cardParams.put("exp_year", LocalDate.now(Clock.systemUTC()).getYear() + 1);
        cardParams.put("cvc", TEST_CC_CVC_NUMBER);
        tokenParams.put("card", cardParams);

        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("description", "Customer for test@example.com");
        customerParams.put("plan", PlansEnum.PRO.getId());

        String stripeCustomerId = stripeService.createCustomer(tokenParams, customerParams);
        assertThat(stripeCustomerId, is(notNullValue()));
    }

}
