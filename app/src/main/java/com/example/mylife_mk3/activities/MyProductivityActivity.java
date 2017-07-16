package com.example.mylife_mk3.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.mylife_mk3.R;
import com.example.mylife_mk3.fragments.AddProductivityRecordFragment;
import com.example.mylife_mk3.fragments.ProductivityDayFragment;
import com.example.mylife_mk3.fragments.ProductivityMonthFragment;
import com.example.mylife_mk3.adapters.ViewPagerAdapter;

public class MyProductivityActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_productivity);
        toolbar = (Toolbar) findViewById(R.id.productivityToolbar);
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

        viewPager = (ViewPager) findViewById(R.id.productivityPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.productivityTabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProductivityDayFragment(), "Day"); //change this later
        adapter.addFragment(new ProductivityMonthFragment(), "Month");
        adapter.addFragment(new AddProductivityRecordFragment(), "Add");
        viewPager.setAdapter(adapter);
    }



}
