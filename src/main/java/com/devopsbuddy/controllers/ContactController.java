package com.devopsbuddy.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.web.domain.fontend.FeedbackPojo;

@Controller
public class ContactController {

    private static final Logger LOG                  = LoggerFactory.getLogger(ContactController.class);

    private static final String FEEDBACK_MODEL_KEY   = "feedback";
    private static final String CONTACT_US_VIEW_NAME = "contact/contact";

    @Autowired
    private EmailService        emailService;

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String contactGet(ModelMap modelMap) {
        FeedbackPojo feedbackPojo = new FeedbackPojo();
        modelMap.addAttribute(ContactController.FEEDBACK_MODEL_KEY, feedbackPojo);
        return ContactController.CONTACT_US_VIEW_NAME;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public String contactPost(@ModelAttribute(FEEDBACK_MODEL_KEY) FeedbackPojo feedbackPojo) {
        LOG.debug("Feedback POJO Content: {}", feedbackPojo);
        emailService.sendFeedbackEmail(feedbackPojo);
        return ContactController.CONTACT_US_VIEW_NAME;
    }

}
