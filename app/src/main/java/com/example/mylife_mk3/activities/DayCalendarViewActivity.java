package com.example.mylife_mk3.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mylife_mk3.models.DayRecordModel;
import com.example.mylife_mk3.R;
import com.example.mylife_mk3.adapters.DayRecordsAdapter;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DayCalendarViewActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private final String getRecordsURL = "http://10.0.2.2:8080/api/getDay";
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_calendar_view);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        queue = Volley.newRequestQueue(this);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        DayScrollDatePicker picker = (DayScrollDatePicker) findViewById(R.id.day_calendar_view_calendar);
        picker.setStartDate(1, 3, 2017);
        picker.setEndDate(31, 12, 2100);

        picker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                if(date != null){
                    getRecordsFromDB(date);
                }
            }
        });

//        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.day_calendar_view_calendar);
//        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
//
//        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
//            @Override
//            public void onDayClick(Date dateClicked) {
//                String month = (String) DateFormat.format("MMM", dateClicked);
//                getSupportActionBar().setTitle(month);
//                getREcordsFromDB(dateClicked);
//            }
//
//            @Override
//            public void onMonthScroll(Date firstDayOfNewMonth) {
//                String month = (String) DateFormat.format("MMM", firstDayOfNewMonth);
//                getSupportActionBar().setTitle(month);
//            }
//        });

    }

    private void getRecordsFromDB(Date dateClicked) {
        SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
        String date = (String) format.format(dateClicked);
        Map<String, String> params = new HashMap<>();
        params.put("date", date);
        params.put("userID", sharedPreferences.getString("databaseID", ""));
        params.put("token", sharedPreferences.getString("token", ""));
        final ListView list = (ListView) findViewById(R.id.day_calendar_view_list);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.day_calendar_view_progress);
        final TextView norecords= (TextView) findViewById(R.id.day_calendar_view_norecords);

        list.setVisibility(ListView.GONE);
        norecords.setVisibility(TextView.GONE);
        progress.setVisibility(ProgressBar.VISIBLE);


        JSONObject object = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getRecordsURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestSuccess = response.getBoolean("success");
                    if(requestSuccess){
                        JSONArray results = response.getJSONArray("records");
                        if(results.length() == 0){
                            norecords.setVisibility(TextView.VISIBLE);
                            progress.setVisibility(ProgressBar.GONE);
                        }
                        else{
                            ArrayList<DayRecordModel> records = new ArrayList<>();
                            for (int i = 0; i<results.length(); ++i){
                                DayRecordModel model = new DayRecordModel((JSONObject)results.get(i));
                                records.add(model);
                            }

                            DayRecordsAdapter adapter = new DayRecordsAdapter(DayCalendarViewActivity.this, records);
                            list.setAdapter(adapter);
                            list.setVisibility(ListView.VISIBLE);

                            progress.setVisibility(ProgressBar.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonObjectRequest);
    }

}
