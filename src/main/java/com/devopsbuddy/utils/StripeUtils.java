package com.devopsbuddy.utils;

import java.util.HashMap;
import java.util.Map;

import com.devopsbuddy.web.domain.fontend.ProAccountPayload;

public class StripeUtils {
    
    public static final String STRIPE_CARD_NUMBER_KEY = "number";
    public static final String STRIPE_EXPIRY_MONTH_KEY = "exp_month";
    public static final String STRIPE_EXPIRY_YEAR_KEY = "exp_year";
    public static final String STRIPE_CVC_KEY = "cvc";
    public static final String STRIPE_CARD_KEY = "card";
    
    private StripeUtils () {
        throw new AssertionError("Non Instantiable");
    }
    
    public static Map<String, Object> extractTokenParamsFromSignupPayload(ProAccountPayload payload) {
        Map<String, Object> tokenParams = new HashMap<> ();
        Map<String, Object> cardParams = new HashMap<> ();
        cardParams.put(STRIPE_CARD_NUMBER_KEY, payload.getCardNumber());
        cardParams.put(STRIPE_EXPIRY_MONTH_KEY, Integer.valueOf(payload.getCardMonth()));
        cardParams.put(STRIPE_EXPIRY_YEAR_KEY, Integer.valueOf(payload.getCardYear()));
        cardParams.put(STRIPE_CVC_KEY, payload.getCardCode());
        tokenParams.put(STRIPE_CARD_KEY, cardParams);
        return tokenParams;
    }
}
