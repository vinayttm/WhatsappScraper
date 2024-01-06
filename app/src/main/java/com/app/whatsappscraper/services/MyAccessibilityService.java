package com.app.whatsappscraper.services;

import android.accessibilityservice.AccessibilityService;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.app.whatsappscraper.utils.AccessibilityMethod;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private List<String> numberList = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isPeriodicFunctionRunning = false;
  AccessibilityEvent event  = null;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        event = accessibilityEvent;
    }




    @Override
    public void onInterrupt() {
        stopMain();
    }


    private void scheduleScrolling(AccessibilityNodeInfo rootNode) {
        AccessibilityNodeInfo contactListNode = AccessibilityMethod.findAndScrollListView(rootNode);
        if (contactListNode != null) {
            contactListNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        } else {
            Log.d("Node Info", "Getting null node");
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("onServiceConnected Method ", "(---@ onServiceConnected @ ---");
        startPeriodicFunction();
    }

    private void startPeriodicFunction() {
        if (!isPeriodicFunctionRunning) {
            handler.postDelayed(mainRunnable, 5000);
            isPeriodicFunctionRunning = true;
        }
    }

    private void stopMain() {
        if (isPeriodicFunctionRunning) {
            handler.removeCallbacks(mainRunnable);
            isPeriodicFunctionRunning = false;
        }
    }

    private Runnable mainRunnable = new Runnable() {
        @Override
        public void run() {
            main();
            if (someConditionToStopPeriodicFunction(false)) {
                stopMain();
            } else {
                handler.postDelayed(this, 5000);
            }
        }
    };


    private void main() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        getNumberString(rootNode);
        scheduleScrolling(rootNode);
        rootNode.recycle();
    }

    private boolean someConditionToStopPeriodicFunction(Boolean isPeriodicFunctionRunning) {
        return isPeriodicFunctionRunning;
    }


    private void getNumberString(AccessibilityNodeInfo rootNode) {
        List<String> allText = AccessibilityMethod.getAllTextInNode(rootNode);
        Log.d("allText", Arrays.toString(allText.toArray()));
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        for (String inputString : allText) {
            try {
                Phonenumber.PhoneNumber parsedPhoneNumber = phoneNumberUtil.parse(inputString, null);
                if (phoneNumberUtil.isValidNumber(parsedPhoneNumber)) {
                    if (!numberList.contains(inputString)) {
                        numberList.add(inputString);
                    }
                } else {

                }
            } catch (Exception e) {
            }
        }
        convertToJson();
    }


    private void convertToJson() {
        System.out.println("Number List = : " + numberList.toString());
        try {
            JSONObject resultJson = new JSONObject();
            JSONArray resultArray = new JSONArray(numberList);
            resultJson.put("Result", resultArray);
            System.out.println(resultJson.toString());
            saveJsonToFile(resultJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveJsonToFile(JSONObject json) {
        String fileName = "whatsapp_number.json";
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs();
        }
        File jsonFile = new File(downloadFolder, fileName);
        try (FileWriter fileWriter = new FileWriter(jsonFile)) {
            fileWriter.write(json.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
