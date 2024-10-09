package com.talmiron.lishcaautoinvities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class SendMsgActivity extends AppCompatActivity {

    Result result;
    String Signature;
    SharedPreferences sharedPref;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("myTag", "SendMsgActivity OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);
        Intent intent = getIntent();
        result = (Result) intent.getSerializableExtra("result");
        sharedPref = getSharedPreferences("signature", MODE_PRIVATE);
        Signature = "\n\n" + sharedPref.getString("signature", "לשכת...");

        for (; counter < result.contacts.length; counter++) {
            if (result.contacts[counter].isChecked()){
                sendMessage(result.contacts[counter].number,result.messageText);
                break;
            }
        }


/*        for (Contact contact:
             result.contacts) {
            if (contact.isChecked())
                contact.setChecked(false);
                sendMessage(contact.number, result.messageText);*/

    }


    public void sendMessage(String phoneNumber, String message){
        Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+972"+ phoneNumber +
                "&text="+ message + Signature));
        Log.d("myTag", phoneNumber);
        counter++;
        startActivityForResult(whatsappIntent,69858);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 69858) {
            Log.d("myTag", "WhatsAppOnResult");
            for (; counter < result.contacts.length; counter++) {
                if (result.contacts[counter].isChecked()) {
                    sendMessage(result.contacts[counter].number, result.messageText);
                    break;
                }
            }
        }
    }
}