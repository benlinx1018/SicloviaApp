package com.siclovia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.siclovia.notification.RegistrationIntentService;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //fade in/out anim
        this.overridePendingTransition(R.anim.enteralpha, R.anim.exitalpha);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashActivity.this, RouteActivity.class);
                    startActivity(intent);

                }
            }
        };

        if (!isNetWorkConnected()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(
                    SplashActivity.this);
            dialog.setMessage(
                    "Please check the network status!")
                    .setTitle("Warning")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    startActivity(new Intent(
                                            Settings.ACTION_WIFI_SETTINGS));
                                    finish();
                                }
                            });
            dialog.show();
        } else {
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
            timerThread.start();
        }

    }

    private boolean isNetWorkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;

        return false;
    }

    @Override
    protected void onPause() {

        super.onPause();
        finish();
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i(Utilities.TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
