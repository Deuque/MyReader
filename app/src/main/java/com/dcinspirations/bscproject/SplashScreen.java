package com.dcinspirations.bscproject;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    TextView skip,val;
    Handler h = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            final Boolean loggedIn = new Sp().getLoggedIn();
            if(loggedIn){
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();
            }else{
                skip.setVisibility(View.VISIBLE);
                val.setVisibility(View.VISIBLE);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        h.postDelayed(r,4000);
        skip = findViewById(R.id.skip);
        val = findViewById(R.id.validate);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();
            }
        });
        val.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashScreen.this,Validation.class).putExtra("state","login"));
                finish();
            }
        });

    }
}
