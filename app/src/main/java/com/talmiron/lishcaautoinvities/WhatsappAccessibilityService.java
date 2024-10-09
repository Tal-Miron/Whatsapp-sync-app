package com.talmiron.lishcaautoinvities;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.List;

public class WhatsappAccessibilityService extends AccessibilityService {

    String Signature;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Signature = "\n\n" + "לשכה" ;
        AccessibilityServiceInfo info = this.getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        this.setServiceInfo(info);
        Log.d("myTag", "onServiceConnected");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("myTag", "OnCreate");
    }

    @Override
    public void onAccessibilityEvent (AccessibilityEvent event) {

        Log.d("TagEvent", "onAccessibilityEvent");
        if (getRootInActiveWindow () == null) {
            return;
        }

        AccessibilityNodeInfoCompat rootInActiveWindow = AccessibilityNodeInfoCompat.wrap (getRootInActiveWindow ());


        // Whatsapp Message EditText id
        List<AccessibilityNodeInfoCompat> messageNodeList = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.whatsapp.w4b:id/entry");
        if (messageNodeList == null || messageNodeList.isEmpty()) {
            return;
        }

        // check if the whatsapp message EditText field is filled with text and ending with your suffix (explanation above)
        AccessibilityNodeInfoCompat messageField = messageNodeList.get(0);
        SharedPreferences sharedPref = getSharedPreferences("signature", MODE_PRIVATE);
        Signature = "\n\n" + sharedPref.getString("signature", "לשכה");
        Log.d("myTag", "signature is: '"+Signature+"'");
        if (messageField.getText() == null || messageField.getText().length() == 0
                || !messageField.getText().toString().contains((Signature))) { // So your service doesn't process any message, but the ones ending your apps suffix
//            Log.d("myTag", "not msg");
//            if (messageField.getText() == null)
//                Log.d("myTag", "msg field is null");
//            else
//                Log.d("myTag", "field is: "+messageField.getText().toString());
            return;
        }

        // Whatsapp send button id
        List<AccessibilityNodeInfoCompat> sendMessageNodeInfoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.whatsapp.w4b:id/send");
        if (sendMessageNodeInfoList == null || sendMessageNodeInfoList.isEmpty()) {
//            Log.d("myTag", "no send btn");
            return;
        }

        AccessibilityNodeInfoCompat sendMessageButton = sendMessageNodeInfoList.get(0);
        if (!sendMessageButton.isVisibleToUser()) {
//            Log.d("myTag", "send btn not visible");
            return;
        }

//        Log.d("myTag", "Fireee!!!");
        // Now fire a click on the send button
        sendMessageButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        // Now go back to the app by clicking on the Android back button twice:
        // First one to leave the conversation screen
        // Second one to leave whatsapp
        try {
            Thread.sleep (500); // hack for certain devices in which the immediate back click is too fast to handle
            performGlobalAction (GLOBAL_ACTION_BACK);
            Thread.sleep (500);  // same hack as above
        } catch (InterruptedException ignored) {}
        performGlobalAction (GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {
        Log.d("myTag", "onInterrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("myTag", "onUnbind");
        return super.onUnbind(intent);
    }
}
