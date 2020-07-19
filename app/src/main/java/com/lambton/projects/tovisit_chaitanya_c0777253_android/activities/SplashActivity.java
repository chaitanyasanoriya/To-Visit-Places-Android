package com.lambton.projects.tovisit_chaitanya_c0777253_android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.lambton.projects.tovisit_chaitanya_c0777253_android.R;

public class SplashActivity extends AppCompatActivity
{
    private static final long DELAYED_DURATION = 1250;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mLinearLayout = findViewById(R.id.linear_layout);
        mLinearLayout.setAlpha(0);
        mLinearLayout.animate().alpha(1).setDuration(1250).start();
        new Handler().postDelayed(() ->
        {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, DELAYED_DURATION);
    }
}