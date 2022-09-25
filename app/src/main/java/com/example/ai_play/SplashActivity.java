package com.example.ai_play;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

class SplashTimer extends Thread
{
    SplashActivity splash;

    SplashTimer( SplashActivity splash)
    {
        this.splash= splash;

    }
    public void run()
    {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{

            Intent iobj = new Intent(splash,MainActivity2.class);
            splash.startActivity(iobj);

            splash.finish();


        }


    }
}

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        SplashTimer timer= new SplashTimer(SplashActivity.this);
        timer.start();


    }
}