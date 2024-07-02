package com.app.car_spares.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.car_spares.R;

//Startup screen that shown at very first for few seconds
public class SplashActivity extends AppCompatActivity {
    public static final int SPLASH_TIME_OUT = 2000;
    ImageView img;
    TextView tv;
    Animation fade_in ,move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        img = findViewById(R.id.img);
        tv = findViewById(R.id.tv);

        fade_in = AnimationUtils.loadAnimation(getApplicationContext() , R.anim.fade_in);
        move = AnimationUtils.loadAnimation(getApplicationContext() , R.anim.move);

        img.startAnimation(fade_in);
        tv.startAnimation(move);

        Thread obj = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME_OUT);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        Intent intent = new Intent(getApplicationContext(), SelectionActivity.class);
                        startActivity(intent);
                        finish();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        };obj.start();
    }
}