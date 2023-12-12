package com.talmiron.lishcaautoinvities;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Result {
    private List<String> phoneNumbers;
    private String messageText;

    public Result(List<String> phoneNumbers, String messageText) {
        this.phoneNumbers = phoneNumbers;
        this.messageText = messageText;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public String getMessageText() {
        return messageText;
    }
}


