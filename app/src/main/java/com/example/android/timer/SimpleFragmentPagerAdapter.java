package com.example.android.timer;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter
{
    /** Context of the app */
    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        if (position == 0)
        {
            return new StopwatchFragment();
        }
        else
        {
            return new CountdownFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        if (position == 0)
        {
            return mContext.getString(R.string.stopwatch);
        }
        else
        {
            return mContext.getString(R.string.countdown_timer);
        }
    }

    @Override
    public int getCount()
    {
        return 2;
    }
}
