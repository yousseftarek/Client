package com.example.mylife_mk3.activities;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mylife_mk3.fragments.MyDayFragment;
import com.example.mylife_mk3.fragments.MyProfileFragment;
import com.example.mylife_mk3.fragments.MyStatsFragment;
import com.example.mylife_mk3.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    private HashMap<String, String> user = new HashMap<String, String>();
//    private String serverToken = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        Intent intent = getIntent();
//
//        getUser().put("name", intent.getStringExtra("username"));
//        getUser().put("email", intent.getStringExtra("email"));
//        getUser().put("birthday", intent.getStringExtra("birthday"));
//        getUser().put("gender", intent.getStringExtra("gender"));
//        getUser().put("databaseID", intent.getStringExtra("databaseID"));
//        getUser().put("homeLat", intent.getStringExtra("homeLat"));
//        getUser().put("homeLong", intent.getStringExtra("homeLong"));
//        getUser().put("workLat", intent.getStringExtra("workLat"));
//        getUser().put("workLong", intent.getStringExtra("workLong"));
//
//        setServerToken(intent.getStringExtra("token"));
        final Fragment temp = new MyStatsFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, temp)
                .commit();

        BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bnv.getMenu().getItem(1).setChecked(true);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment frag = temp;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_profile:
                        MyProfileFragment profile = new MyProfileFragment();
                        getFragmentManager().beginTransaction().remove(frag)
                                .add(R.id.fragmentContainer, profile)
                                .commit();
                        frag = profile;
                        return true;
                    case R.id.action_stats:
                        MyStatsFragment stats = new MyStatsFragment();
                        getFragmentManager().beginTransaction().remove(frag)
                                .add(R.id.fragmentContainer, stats)
                                .commit();
                        frag = stats;
                        return true;
                    case R.id.action_day:
                        MyDayFragment day = new MyDayFragment();
                        getFragmentManager().beginTransaction().remove(frag)
                                .add(R.id.fragmentContainer, day)
                                .commit();
                        frag = day;
                        return true;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {

            SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
