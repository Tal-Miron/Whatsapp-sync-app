package com.talmiron.lishcaautoinvities;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChooseActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton ScanBtn, SettingsBtn, HistoryBtn;
    GmsBarcodeScanner scanner;
    WhatsappAccessibilityService was;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        InitViews();
        InitObjects();
        SqlManager.initialize(this);
    }

    private void InitObjects() {
         scanner = GmsBarcodeScanning.getClient(this);
    }

    private void InitViews() {
        ScanBtn = findViewById(R.id.scanbtn);
        ScanBtn.setOnClickListener(this);
        SettingsBtn = findViewById(R.id.settingsbtn);
        SettingsBtn.setOnClickListener(this);
        HistoryBtn = findViewById(R.id.historybtn);
        HistoryBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view == SettingsBtn){
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        if (view == HistoryBtn){
            Intent i = new Intent(this, HistoryActivity.class);
            startActivity(i);
        }

        if(view == ScanBtn  && isAccessibilityOn(this, WhatsappAccessibilityService.class)){
            scanner.startScan()
                    .addOnSuccessListener(
                            barcode -> {
                                // Task completed successfully
                                String rawValue = barcode.getRawValue();
                                try {
                                    Result result = new Result(rawValue, this);
                                    Intent i = new Intent(this, ManageMessage.class);
                                    i.putExtra("result", result);
                                    if(SqlManager.getRowCount() >= getSharedPreferences("saveamt", MODE_PRIVATE).getInt("saveamt", 0)) {
                                        SqlManager.DeleteRows(1);
                                    }
                                    OldMeetingFormatParser(rawValue);
                                    startActivity(i);
                                }
                                catch (Exception e) { // Failed. Save failed to list and show to the user later on
                                    Log.d("myTag", "Exception : " + e.getMessage());
                                    //Pop Up
                                }
                            })
                    .addOnCanceledListener(
                            () -> {
                                // Task canceled
                                //Pop Up
                            })
                    .addOnFailureListener(
                            e -> {
                                // Task failed with an exception
                                //Pop UP
                            });
        }
    }

    private void OldMeetingFormatParser(String rawText){
        String title = "";
        String name = "";
        String date = "";
        String time = "";

        // Pattern for title
        Pattern titlePattern = Pattern.compile("\\*שימו לב – (.*?)!\\*");
        Matcher titleMatcher = titlePattern.matcher(rawText);
        if (titleMatcher.find()) {
            title = titleMatcher.group(1);
        }

        // Pattern for name
        Pattern namePattern = Pattern.compile("\\*\\*\\n(.*?)\\n");
        Matcher nameMatcher = namePattern.matcher(rawText);
        if (nameMatcher.find()) {
            name = nameMatcher.group(1);
        } else {
            // Adjusted pattern to capture name correctly
            Pattern adjustedNamePattern = Pattern.compile("\\n(.*?)\\nתאריך:");
            Matcher adjustedNameMatcher = adjustedNamePattern.matcher(rawText);
            if (adjustedNameMatcher.find()) {
                name = adjustedNameMatcher.group(1);
            }
        }

        // Pattern for date
        Pattern datePattern = Pattern.compile("תאריך:(\\d{2}/\\d{2})");
        Matcher dateMatcher = datePattern.matcher(rawText);
        if (dateMatcher.find()) {
            date = dateMatcher.group(1);
        }

        // Pattern for time
        Pattern timePattern = Pattern.compile("שעות: (\\d{2}:\\d{2})-\\d{2}:\\d{2}");
        Matcher timeMatcher = timePattern.matcher(rawText);
        if (timeMatcher.find()) {
            time = timeMatcher.group(1);
        }

        SqlManager.insert(title, name, date,time,rawText);
        Log.d("myTag", title + "," + name +" ---end---");
    }

    private boolean isAccessibilityOn (Context context, Class < ? extends
            AccessibilityService> clazz){
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + clazz.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {
        }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            //AccesabilityEnabled
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString(settingValue);
                while (colonSplitter.hasNext()) {
                    String accessibilityService = colonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        //IsAccessabilityOff
        return false;
    }
}