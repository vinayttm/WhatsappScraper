package com.app.whatsappscraper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.app.whatsappscraper.services.MyAccessibilityService;
import com.app.whatsappscraper.utils.AccessibilityMethod;
import com.app.whatsappscraper.utils.MyDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        boolean isServiceEnabled = AccessibilityMethod.isAccessibilityServiceEnabled(
                this, MyAccessibilityService.class);
        if (!isServiceEnabled) {
            MyDialog.showDialog(MainActivity.this, "Accessibility Permission Required.", "To use this app, you need to enable Accessibility Service. Go to Settings to enable it?",
                    new MyDialog.DialogClickListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            openAccessibilitySettings();
                        }
                        @Override
                        public void onNegativeButtonClick() {
                        }
                    });

        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    openApp();
            }
        });
    }

    private void openApp() {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage("com.whatsapp");
        if (intent != null) {
            startActivity(intent);
        } else {
            System.out.println("App with package name " + "com.whatsapp" + " not found.");
        }
    }
    private void openAccessibilitySettings() {
        Intent accessibilityIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(accessibilityIntent);
    }


}