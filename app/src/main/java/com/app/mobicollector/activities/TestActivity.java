package com.app.mobicollector.activities;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.app.mobicollector.R;
import com.app.mobicollector.database.DBHelper;
import com.app.mobicollector.fragments.CollectionFragment;
import com.app.mobicollector.fragments.SyncFragment;
import com.app.mobicollector.util.Session;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends ActionBarActivity implements ActionBar.TabListener,ViewPager.OnPageChangeListener {

    private String TAG = "TestActivity";
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private MyPageAdapter pageAdapter;
    // Tab titles
    private String[] tabs = {"Collection", "Sync"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_test);
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(this);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
        /*List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager =
                (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(pageAdapter);*/
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }


    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.logout:
                Log.d(TAG, "Logout Done..");
                DBHelper db = new DBHelper(this);
                db.deleteAllCustomers();
                Session registeredAgent = new Session(this);
                registeredAgent.clearAgentId();
                Intent agentAcitivity = new Intent(this, AgentLoginActivity.class);
                startActivity(agentAcitivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(CollectionFragment.newInstance("Collection"));
        fList.add(SyncFragment.newInstance("Sync"));
        //fList.add(MyFragment.newInstance("Fragment 3"));
        return fList;
    }

}
