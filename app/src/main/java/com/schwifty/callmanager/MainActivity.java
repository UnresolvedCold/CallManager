package com.schwifty.callmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private String permissions[] = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button vGrantPermissions = findViewById(R.id.GrantPermissions);

        if(arePermissionsGranted())
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent intent =new Intent(MainActivity.this,Settings.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }, 500);
        }
        else
        {
           /* final JSONObject entries = new JSONObject();
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString("CallManager_Unknown",entries.toString()).apply();*/


            vGrantPermissions.setVisibility(View.VISIBLE);
            vGrantPermissions.setEnabled(true);

            vGrantPermissions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                 GrantPermissions();

                }
            });

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent =new Intent(MainActivity.this,Settings.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "These permissions are required for basic functioning of the app.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private Boolean arePermissionsGranted() {

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
    return true;
    }

    private void GrantPermissions() {
        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

    }

}
