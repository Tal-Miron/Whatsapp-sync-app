package com.talmiron.lishcaautoinvities;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    ImageButton startBtn;
    CheckBox GMSModuleInstalledOnCB, AccessibiltyOnCB, CotactsPermissionCB, PVFPermissionCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);
        InitView();
        CheckBoxes();
        InitListeners();
        Log.d("myTag", "This is a Test");
    }

    private void InitListeners() {
        AccessibiltyOnCB.setOnCheckedChangeListener(this);
        // GMSModuleInstalledOnCB.setOnCheckedChangeListener(this);
        // CotactsPermissionCB.setOnCheckedChangeListener(this);
        // PVFPermissionCB.setOnCheckedChangeListener(this);
    }

    private void CheckBoxes() {
        PVFPermissionCB.setChecked(true);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED) {
            CotactsPermissionCB.setChecked(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }

        if (!AccessibiltyOnCB.isChecked())
            AccessibiltyOnCB.setChecked(isAccessibilityOn(this, WhatsappAccessibilityService.class));

        GMSModuleInstalledOnCB.setChecked(true);
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(this);
        com.google.android.gms.common.moduleinstall.ModuleInstallClient moduleInstall = ModuleInstall.getClient(this);
        Log.d("myTag", "Starting installation");
        ModuleInstallRequest moduleInstallRequest = ModuleInstallRequest.newBuilder()
                .addApi(GmsBarcodeScanning.getClient(this))
                .build();
        moduleInstall
                .installModules(moduleInstallRequest)
                .addOnSuccessListener(
                        response -> {
                            if (response.areModulesAlreadyInstalled()) {
                                GMSModuleInstalledOnCB.setChecked(true);
                                Log.d("myTag", "GMS ARE INSTALLED");
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            GMSModuleInstalledOnCB.setChecked(false);
                            Log.d("myTag", "FAILED : GMS ARE -NOT- INSTALLED");
                        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CotactsPermissionCB.setChecked(true);
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    private void InitView() {
        startBtn = findViewById(R.id.StartBtn);
        startBtn.setOnClickListener(this);
        GMSModuleInstalledOnCB = findViewById(R.id.GMSModuleInstalledOnCB);
        AccessibiltyOnCB = findViewById(R.id.AccessibiltyOnCB);
        CotactsPermissionCB = findViewById(R.id.CotactsPermissionCB);
        PVFPermissionCB = findViewById(R.id.PVFPermissionCB);

    }



 /*   public String MessageBuilder(JSONObject message) throws Exception{
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
    } */

    @Override
    public void onClick(View view) {

        if (view == startBtn) {
            if (AllBtnsChecked()) {
                Intent intent = new Intent(this, ChooseActivity.class);
                startActivity(intent);
            }
        }

        }

        private boolean AllBtnsChecked () {
            return GMSModuleInstalledOnCB.isChecked() && AccessibiltyOnCB.isChecked() && CotactsPermissionCB.isChecked() && PVFPermissionCB.isChecked();
        }

        private boolean isAccessibilityOn (Context context, Class < ? extends
        AccessibilityService > clazz){
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


        @Override
        public void onCheckedChanged (CompoundButton compoundButton,boolean b){
            if (AccessibiltyOnCB.isChecked()) {
                if (!isAccessibilityOn(this, WhatsappAccessibilityService.class)) {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    this.startActivity(intent);
                }
            }
        }

        @Override
        protected void onRestart() {
            super.onRestart();
            CheckBoxes();
        }

    }
