
package com.talmiron.lishcaautoinvities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Result implements Serializable {
    public Contact[] contacts;
    public String messageText;

    public Result(String rawData, Context context){
            List<String> phoneNumbers = new ArrayList<>();

            String pattern = "<<(\\d+(?:,\\d+)*)>>"; // Updated pattern to match any number of digits separated by commas
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(rawData);

            while (matcher.find()) {
                String numbersInside = matcher.group(1);
                String[] numbersArray = numbersInside.split(",");

                for (String number : numbersArray) {
                    phoneNumbers.add(number);
                }
            }

            // Remove all occurrences of << >> and everything inside
            String cleanedText = regex.matcher(rawData).replaceAll("").trim();
            String[] arr = (String[]) phoneNumbers.toArray(new String[0]);

        this.contacts = getContacts(context, arr);
        this.messageText = cleanedText;

    }

    public Result(String[] phoneNumbers, String messageText, Context context) {
        this.contacts = getContacts(context, phoneNumbers);
        this.messageText = messageText;
    }

    private Contact[] getContacts(Context context, String[] phoneNumbers) {
        List<Contact> list = new ArrayList<>();

        for (String number : phoneNumbers) {
            list.add(new Contact(number, context));
        }

        // Create a new Contact[] array and copy elements from the list
        Contact[] contacts = new Contact[list.size()];
        list.toArray(contacts);

        return contacts;
    }
}
