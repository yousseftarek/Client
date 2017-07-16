package com.example.mylife_mk3.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mylife_mk3.R;
import com.example.mylife_mk3.models.SleepRecordModel;
import com.example.mylife_mk3.adapters.SleepRecordsAdapter;
import com.harrywhewell.scrolldatepicker.MonthScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SleepMonthFragment extends Fragment {

    private String getMonthRecordsURL = "http://10.0.2.2:8080/api/getSleepMonth";
    View viewReference;
    RequestQueue queue;

    private SharedPreferences sharedPreferences;
    ListView listView;

    public SleepMonthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_sleep__month, container, false);
        viewReference = rootView;

        sharedPreferences  = getActivity().getSharedPreferences(getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);
        listView  = (ListView) rootView.findViewById(R.id.sleepMonth_listView);
        queue = Volley.newRequestQueue(getActivity());

        MonthScrollDatePicker picker = (MonthScrollDatePicker) rootView.findViewById(R.id.sleepMonth_datepicker);
        picker.setStartDate(1, 2017);
        picker.setEndDate(12, 2100);


        picker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                if(date != null){
                    dateSelected(date);
                }
            }
        });

        return rootView;
    }

    private void dateSelected(Date selectedDate) {
        final ProgressBar progressBar = (ProgressBar) viewReference.findViewById(R.id.sleepMonth_progress);
        final TextView noRecords = (TextView) viewReference.findViewById(R.id.sleepMonth_noRecords);

        noRecords.setVisibility(TextView.GONE);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        listView.setVisibility(ListView.GONE);

        DateFormat format = new SimpleDateFormat("yyyy-MM");
        String temp = format.format(selectedDate);
        String[] date = temp.split("-");

        Map<String, String> params = new HashMap<String, String>();
        params.put("userID", sharedPreferences.getString("databaseID", ""));
        params.put("month", date[1]);
        params.put("year", date[0]);
        params.put("token", sharedPreferences.getString("token", ""));

        JSONObject object = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getMonthRecordsURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestSuccess = response.getBoolean("success");
                    if(requestSuccess){
                        JSONArray results = response.getJSONArray("records");

                        progressBar.setVisibility(ProgressBar.GONE);
                        if(results.length() == 0){
                            noRecords.setVisibility(TextView.VISIBLE);
                            listView.setVisibility(ListView.GONE);
                        }
                        else{
                            noRecords.setVisibility(TextView.GONE);

                            ArrayList<SleepRecordModel> records = new ArrayList<SleepRecordModel>();
                            String id;
                            String startTime;
                            String endTime;
                            String startDate;
                            String endDate;
                            String duration;
                            JSONObject time;
                            JSONObject date;

                            for (int i=0; i<results.length(); ++i){
                                id = ((JSONObject)results.get(i)).getString("_id");
                                time = ((JSONObject)results.get(i)).getJSONObject("start_time");
                                date = ((JSONObject)results.get(i)).getJSONObject("start_date");
                                startDate = date.getInt("day") + "-" + date.getInt("month") + "-" + date.getInt("year");
                                startTime = time.getInt("hour") + ":" + time.getInt("minute") + ":" + time.getInt("second");

                                time = ((JSONObject)results.get(i)).getJSONObject("end_time");
                                date = ((JSONObject)results.get(i)).getJSONObject("end_date");
                                endDate = date.getInt("day") + "-" + date.getInt("month") + "-" + date.getInt("year");
                                endTime = time.getInt("hour") + ":" + time.getInt("minute") + ":" + time.getInt("second");

                                duration = ((JSONObject)results.get(i)).getString("duration");

                                SleepRecordModel model = new SleepRecordModel(id, startTime, endTime, startDate, endDate, duration);
                                records.add(model);
                            }

                            SleepRecordsAdapter adapter = new SleepRecordsAdapter(SleepMonthFragment.this.getActivity(), records);

                            listView.setAdapter(adapter);
                            listView.setVisibility(ListView.VISIBLE);
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
