package com.example.mylife_mk3.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.mylife_mk3.R;
import com.example.mylife_mk3.fragments.SleepDayFragment;
import com.example.mylife_mk3.fragments.SleepMonthFragment;
import com.example.mylife_mk3.fragments.AddSleepRecordFragment;
import com.example.mylife_mk3.adapters.ViewPagerAdapter;

public class MySleepActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sleep);
        toolbar = (Toolbar) findViewById(R.id.sleepToolbar);
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

        viewPager = (ViewPager) findViewById(R.id.sleepPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.sleepTabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SleepDayFragment(), "Day");
        adapter.addFragment(new SleepMonthFragment(), "Month");
        adapter.addFragment(new AddSleepRecordFragment(), "Add");
        viewPager.setAdapter(adapter);
    }


}
