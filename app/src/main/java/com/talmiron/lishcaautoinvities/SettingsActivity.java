package com.talmiron.lishcaautoinvities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    EditText SignatureET;
    TextView TVAmount;
    Switch WBSwitch;
    Button SaveAndCloseBtn;
    SharedPreferences sharedPrefSignature,sharedPrefAmt, sharedPrefWB;
    SeekBar SBSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPrefSignature = getSharedPreferences("signature", MODE_PRIVATE);
        sharedPrefAmt = getSharedPreferences("saveamt", MODE_PRIVATE);
        sharedPrefWB = getSharedPreferences("usewb", MODE_PRIVATE);
        InitViews();
    }

    private void InitViews() {
        SignatureET = findViewById(R.id.editTextSignature);
        SignatureET.setText(sharedPrefSignature.getString("signature", "לשכת..."));
        WBSwitch = findViewById(R.id.WBSwitch);
        WBSwitch.setOnClickListener(this);
        WBSwitch.setChecked(sharedPrefWB.getBoolean("usewb", false));
        SBSize = findViewById(R.id.seekBar);
        SBSize.setOnSeekBarChangeListener(this);
        TVAmount = findViewById(R.id.textViewAmount);
        SaveAndCloseBtn = findViewById(R.id.SaveAndCloseBtn);
        SaveAndCloseBtn.setOnClickListener(this);

        //save for last
        SBSize.setProgress(sharedPrefAmt.getInt("saveamt", 0));
    }


    @Override
    public void onClick(View v) {
        String WHATSAPP_BUSINESS_PACKAGE_NAME = "com.whatsapp.w4b";

        int amtNum = Integer.parseInt(TVAmount.getText().toString());
        int rowCnt = SqlManager.getRowCount();
        /*if (v == WBSwitch && WBSwitch.isChecked()){
            context
            // if whatsapp business is installed
            if (isAppInstalled(WHATSAPP_BUSINESS_PACKAGE_NAME, this)) {
                shareTextOnWhatsappBusiness(shareText);
        }*/
        if (v == SaveAndCloseBtn){
            sharedPrefSignature.edit().putString("signature", SignatureET.getText().toString()).apply();
            sharedPrefAmt.edit().putInt("saveamt", amtNum).apply();
        }
        if(rowCnt > amtNum){
            SqlManager.DeleteRows(rowCnt - amtNum);}

        this.finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TVAmount.setText(GetFormatedText(progress));
    }

    private String GetFormatedText(int progress){
        String amount = Integer.toString(progress);
        for (int i = amount.length(); i < 3; i++){
            amount = "0" + amount;
        }
        return amount;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}