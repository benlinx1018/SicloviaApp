package com.siclovia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashActivity.this,RouteActivity.class);
                    startActivity(intent);
                    SplashActivity.this.overridePendingTransition(R.anim.enteralpha, R.anim.exitalpha);
                }
            }
        };
        timerThread.start();

    }
    @Override
    protected void onPause() {

        super.onPause();
        finish();
    }
}
