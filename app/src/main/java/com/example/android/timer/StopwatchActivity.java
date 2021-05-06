package com.example.android.timer;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class StopwatchActivity extends AppCompatActivity
{
    StopwatchFragment sf = new StopwatchFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new StopwatchFragment())
                .commit();
    }
}
