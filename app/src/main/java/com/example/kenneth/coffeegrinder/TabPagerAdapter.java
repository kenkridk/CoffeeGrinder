package com.example.kenneth.coffeegrinder;

/**
 * Created by Nikander on 17-04-2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                Log.d("CoffeeApp_0", "Yes");
                return new YesActivity();
            case 1:
                //Fragment for Ios tab
                return new NotificationActivity();
            case 2:
                Log.d("CoffeeApp_0", "YNo");
                //Fragment for Windows tab
                return new NoActivity();
        }
        return null;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 3; //No of Tabs
    }
}