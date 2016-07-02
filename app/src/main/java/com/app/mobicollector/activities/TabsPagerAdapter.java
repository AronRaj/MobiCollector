package com.app.mobicollector.activities;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.app.mobicollector.fragments.CollectionFragment;
import com.app.mobicollector.fragments.SyncFragment;

/**
 * Created by Aron on 22-05-2016.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Collection fragment activity
                return new CollectionFragment();
            case 1:
                // Sync fragment activity
                return new SyncFragment();
            /*case 2:
                // New fragment activity
                return new CollectionFragment();*/
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}