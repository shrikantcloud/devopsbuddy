package com.devopsbuddy.web.i18n;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class I18NService {

    @Autowired
    private MessageSource messageSource;

    public String getMessageSource(String messageID) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessageSource(messageID, locale);
    }

    public String getMessageSource(String messageID, Locale locale) {
        return messageSource.getMessage(messageID, null, locale);
    }

}
