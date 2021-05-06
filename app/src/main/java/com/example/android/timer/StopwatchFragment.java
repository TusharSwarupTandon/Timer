package com.example.android.timer;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;
import java.util.Objects;

public class StopwatchFragment extends Fragment
{
    private long timeWhenStopped = 0;
    
    private Chronometer mStopwatchTimerChronometer;
    private FloatingActionButton mPauseButton;
    private FloatingActionButton mStartButton;
    private FloatingActionButton mRefreshButton;

    public StopwatchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_stopwatch, container, false);
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPauseButton = rootView.findViewById(R.id.pauseStopwatch);
        mStartButton = rootView.findViewById(R.id.startStopwatch);
        mRefreshButton = rootView.findViewById(R.id.refreshStopwatch);
        mStopwatchTimerChronometer = rootView.findViewById(R.id.stopwatch_timer);
        mStopwatchTimerChronometer.setOnChronometerTickListener(chronometer -> {
            long time = SystemClock.elapsedRealtime() - chronometer.getBase();
            int hours = (int)(time /3600000);
            int minutes = (int)(time - hours * 3600000)/60000;
            int seconds = (int)(time - hours*3600000 - minutes *60000)/1000 ;
            String timeToDisplay = String.format(Locale.getDefault(),
                    "%02d:%02d:%02d",
                    hours, minutes, seconds);
            chronometer.setText(timeToDisplay);
        });
        mStopwatchTimerChronometer.setBase(SystemClock.elapsedRealtime());
        onRefreshClicked();
        mPauseButton.setOnClickListener(v -> onPauseClicked());


        mStartButton.setOnClickListener(v -> onStartClicked());

        mRefreshButton.setOnClickListener(v -> onRefreshClicked());
        return rootView;
    }

    private void onPauseClicked()
    {
        timeWhenStopped = mStopwatchTimerChronometer.getBase() - SystemClock.elapsedRealtime();
        mStopwatchTimerChronometer.stop();
        mStartButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.GONE);
        mRefreshButton.setVisibility(View.VISIBLE);
    }

    private void onStartClicked()
    {
        mStopwatchTimerChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        mStopwatchTimerChronometer.start();
        mStartButton.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.VISIBLE);
        mRefreshButton.setVisibility(View.VISIBLE);
    }

    private void onRefreshClicked()
    {
        mStopwatchTimerChronometer.stop();
        timeWhenStopped = 0;
        mStopwatchTimerChronometer.setText(R.string.default_start_time);
        mStartButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.GONE);
        mRefreshButton.setVisibility(View.GONE);
    }
}