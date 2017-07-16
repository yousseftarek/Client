package com.example.mylife_mk3.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.mylife_mk3.R;
import com.example.mylife_mk3.fragments.AddLocationRecordFragment;
import com.example.mylife_mk3.fragments.LocationDayFragment;
import com.example.mylife_mk3.fragments.LocationMonthFragment;
import com.example.mylife_mk3.adapters.ViewPagerAdapter;

public class MyLocationsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_locations);
        toolbar = (Toolbar) findViewById(R.id.locationsToolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager)findViewById(R.id.locationsPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.locationsTabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LocationDayFragment(), "Day");
        adapter.addFragment(new LocationMonthFragment(), "Month");
        adapter.addFragment(new AddLocationRecordFragment(), "Add");
        viewPager.setAdapter(adapter);
    }


}
