package com.example.android.timer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CountdownFragment extends Fragment
{
    private long mMilliSecondsLeft;
    private boolean mWasRunning;
    private int mTimerHours;
    private int mTimerMinutes;
    private int mTimerSeconds;
    private boolean mDefaultChanged;

    private FloatingActionButton mStartButton;
    private FloatingActionButton mPauseButton;
    private FloatingActionButton mRefreshButton;
    private TextView mCountdownWatchTimer;
    private LinearLayout mTimerLayout;
    private CountDownTimer mCountDownTimer;
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;

    private final MediaPlayer.OnCompletionListener mCompletionListener = mediaPlayer -> releaseMediaPlayer();

    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange)
        {
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN)
            {
                mMediaPlayer.start();
            }
            else if (focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
            {
                releaseMediaPlayer();
            }
        }
    };


    public CountdownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_countdown_timer, container, false);
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mDefaultChanged = false;
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        NumberPicker hoursNumberPicker = rootView.findViewById(R.id.hours);
        NumberPicker minutesNumberPicker = rootView.findViewById(R.id.minutes);
        NumberPicker secondsNumberPicker = rootView.findViewById(R.id.seconds);
        mTimerLayout = rootView.findViewById(R.id.timer);
        mCountdownWatchTimer = rootView.findViewById(R.id.countdown_timer);
        hoursNumberPicker.setMinValue(0);
        hoursNumberPicker.setMaxValue(23);
        hoursNumberPicker.setFormatter(i -> String.format(Locale.getDefault(),"%02d", i));
        hoursNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> mTimerHours = newVal);

        minutesNumberPicker.setMinValue(0);
        minutesNumberPicker.setMaxValue(59);
        minutesNumberPicker.setFormatter(i -> String.format(Locale.getDefault(),"%02d", i));
        minutesNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            mDefaultChanged = true;
            mTimerMinutes = newVal;
        });
        minutesNumberPicker.setWrapSelectorWheel(true);
        minutesNumberPicker.setValue(5);

        secondsNumberPicker.setMinValue(0);
        secondsNumberPicker.setMaxValue(59);
        secondsNumberPicker.setFormatter(i -> String.format(Locale.getDefault(),"%02d", i));
        secondsNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> mTimerSeconds = newVal);

        mPauseButton = rootView.findViewById(R.id.pauseCountdown);
        mStartButton = rootView.findViewById(R.id.startCountdown);
        mRefreshButton = rootView.findViewById(R.id.refreshCountdown);

        onRefreshClicked();

        mPauseButton.setOnClickListener(v -> onStopClicked());


        mStartButton.setOnClickListener(v -> onStartClicked());

        mRefreshButton.setOnClickListener(v -> onRefreshClicked());
        updateCountdownText();
        return rootView;
    }

    private void releaseMediaPlayer()
    {
        if (mMediaPlayer != null)
        {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void onStopClicked()
    {
        mCountDownTimer.cancel();
        mWasRunning = true;
        mPauseButton.setVisibility(View.GONE);
        mStartButton.setVisibility(View.VISIBLE);
        mRefreshButton.setVisibility(View.VISIBLE);
    }

    private void onStartClicked()
    {
        if(!mWasRunning)
        {
            if (!mDefaultChanged)
                mTimerMinutes = 5;
            mMilliSecondsLeft = (mTimerHours * 3600 + mTimerMinutes * 60 + mTimerSeconds) * 1000;
        }
        if(mMilliSecondsLeft > 0)
        {
            mCountDownTimer = new CountDownTimer(mMilliSecondsLeft, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {
                    mMilliSecondsLeft = millisUntilFinished;
                    updateCountdownText();
                }

                @Override
                public void onFinish()
                {
                    mWasRunning = false;
                    onRefreshClicked();
                    mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.countdown_complete_audio);
                    int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.countdown_complete_audio);
                        mMediaPlayer.start();
                        mMediaPlayer.setOnCompletionListener(mCompletionListener);
                    }
                }
            }.start();

            mWasRunning = false;

            mStartButton.setVisibility(View.GONE);
            mPauseButton.setVisibility(View.VISIBLE);
            mRefreshButton.setVisibility(View.INVISIBLE);
            mTimerLayout.setVisibility(View.GONE);
            mCountdownWatchTimer.setVisibility(View.VISIBLE);
        }
    }

    private void onRefreshClicked()
    {
        mWasRunning = false;
        mMilliSecondsLeft = 0;
        mStartButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.GONE);
        mRefreshButton.setVisibility(View.GONE);
        mTimerLayout.setVisibility(View.VISIBLE);
        mCountdownWatchTimer.setVisibility(View.GONE);
    }

    private void updateCountdownText()
    {
        int hours   = (int) (mMilliSecondsLeft / 1000) / 3600;
        int minutes = (int) ((mMilliSecondsLeft / 1000) % 3600) / 60;
        int seconds = (int) (mMilliSecondsLeft / 1000) % 60;
        String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        mCountdownWatchTimer.setText(time);
    }
}