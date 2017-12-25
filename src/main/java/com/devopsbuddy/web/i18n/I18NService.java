package com.devopsbuddy.web.i18n;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class I18NService {

    private static final Logger LOG = LoggerFactory.getLogger(I18NService.class);

    @Autowired
    private MessageSource       messageSource;

    public String getMessageSource(String messageID) {
        LOG.info("Returning i18n text for messageID : {}", messageID);
        Locale locale = LocaleContextHolder.getLocale();
        return getMessageSource(messageID, locale);
    }

    public String getMessageSource(String messageID, Locale locale) {
        return messageSource.getMessage(messageID, null, locale);
    }

}
