package com.talmiron.lishcaautoinvities;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.talmiron.lishcaautoinvities.databinding.ActivityMainBinding;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    Button btn;
    TextView tvResult;
    GmsBarcodeScanner scanner;
    String rawValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btn = findViewById(R.id.send);
        btn.setOnClickListener(this);
        tvResult = findViewById(R.id.tvResult);

        scanner = GmsBarcodeScanning.getClient(this);

        if (!isAccessibilityOn (this, WhatsappAccessibilityService.class)) {
            Intent intent = new Intent (Settings.ACTION_ACCESSIBILITY_SETTINGS);
            this.startActivity (intent);
        }
    }


    public void sendMessage(String phoneNumber, String message){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+972"+ phoneNumber +
                "&text="+ message + "\n\nלשכת ענף אד״ם"));
        startActivity(i);
    }

    public String MessageBuilder(JSONObject message) throws Exception{
        String phone = message.getString("P"); // Better than has since also checks type.
        StringBuilder msg = new StringBuilder();
        switch (message.getInt("K")){ // Message Kind: 0 - New, 1 - Update, 2 - Canceled
            case 0: // New Appointment
                msg.append("*פגישה חדשה*" + "\n");
                msg.append(message.getString("T") + " ");
                msg.append(" נקבעה לתאריך ");
                msg.append(message.getString("D")); // D - Date; Must have
                if (message.has("S")){ // S - Start time
                    msg.append(" ותיערך בשעה ");
                    msg.append(message.getString("S"));
                }
                if (message.has("E")){ // E - End time
                    msg.append(" עד השעה ");
                    msg.append(message.getString("E"));
                }
                msg.append("\n");
                msg.append("אשמח אם תאשר/י הגעה");
                break;
            case 1: // Appointment update
                msg.append("*עדכון זמן פגישה*\n");
                msg.append(message.getString("T") + " ");
                if (message.has("D")) { // Updated date
                    msg.append("עודכנה לתאריך ");
                    msg.append(message.getString("D")); // D - Date
                }
                if (message.has("S")){ // S - Start time
                    msg.append("ותיערך בשעה ");
                    msg.append(message.getString("S"));
                }
                if (message.has("E")){ // E - End time
                    msg.append("עד השעה ");
                    msg.append(message.getString("E"));
                }
                msg.append("\n");
                msg.append("אשמח אם תאשר/י הגעה");
                break;
            case 2: //
                msg.append("*עדכון פגישה נעה״ח*");
                msg.append(message.getString("T") + "\n");
                msg.append(" נדחתה עד הודעה חדשה.");
                break;
        }
        return msg.toString();
    }

    @Override
    public void onClick(View view) {
        if (view == btn)
        {
            // Example JSON
//            String json="{ \"m\":\n" +
//                    "    [\n" +
//                    "        {\n" +
//                    "            \"P\": \"0533342793\",\n" +
//                    "            \"K\": 0,\n" +
//                    "            \"T\": \"פגישת היכרות נושא א ב ג\",\n" +
//                    "            \"D\": \"01/01/2024\",\n" +
//                    "            \"S\": \"10:30\",\n" +
//                    "            \"E\": \"12:00\",\n" +
//                    "            \"L\": \"קרייה\"\n" +
//                    "        }\n" +
//                    "    ]\n" +
//                    "}";
            scanner.startScan()
                    .addOnSuccessListener(
                            barcode -> {
                                // Task completed successfully
                                rawValue = barcode.getRawValue();
                                try {
                                    JSONObject jsonResponse = new JSONObject(rawValue);
                                    JSONArray messages = jsonResponse.getJSONArray("m"); // messages
                                    for(int i=0;i<messages.length();i++){
                                        JSONObject msgJson = messages.getJSONObject(i);
                                        String message = MessageBuilder(msgJson);
                                        sendMessage(msgJson.getString("P"), message);
                                        tvResult.setText(rawValue);
                                    }
                                } catch (Exception e) { // Failed. Save failed to list and show to the user later on
                                    tvResult.setText(e.toString());
                                }

                            })
                    .addOnCanceledListener(
                            () -> {
                                // Task canceled
                                tvResult.setText("Canceled");
                            })
                    .addOnFailureListener(
                            e -> {
                                // Task failed with an exception
                                tvResult.setText("Failed");
                            });
        }
    }

    private boolean isAccessibilityOn (Context context, Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName () + "/" + clazz.getCanonicalName ();
        try {
            accessibilityEnabled = Settings.Secure.getInt (context.getApplicationContext ().getContentResolver (), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {  }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter (':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString (context.getApplicationContext ().getContentResolver (), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString (settingValue);
                while (colonSplitter.hasNext ()) {
                    String accessibilityService = colonSplitter.next ();

                    if (accessibilityService.equalsIgnoreCase (service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}