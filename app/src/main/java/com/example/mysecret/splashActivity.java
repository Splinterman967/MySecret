package com.example.mysecret;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

@SuppressLint("CustomSplashScreen")
public class splashActivity extends AppCompatActivity {

    Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//2 saniye gecikmeli bu kodu yaz
        handler.postDelayed(() -> {
            Intent intent=new Intent(splashActivity.this,recyclerview_notes.class);
            startActivity(intent);
            finish();

        },600);
    }
}