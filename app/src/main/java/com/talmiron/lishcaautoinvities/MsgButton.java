package com.talmiron.lishcaautoinvities;

import android.content.Context;

import androidx.appcompat.widget.AppCompatButton;

import java.time.LocalDateTime;

public class MsgButton extends AppCompatButton {
    private int id;
    private String headline;
    private LocalDateTime dateTime;

    public MsgButton(Context context, String headline, String date, String hour) {
        super(context);
        // Set button text (headline, date, hour)
        setText(String.format("%s\n%s %s", headline, date, hour));
        // Apply any other styling or customization
    }
}
