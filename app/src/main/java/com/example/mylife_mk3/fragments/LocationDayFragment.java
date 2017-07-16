package com.example.mylife_mk3.fragments;


import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
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
import com.example.mylife_mk3.models.LocationRecordModel;
import com.example.mylife_mk3.R;
import com.example.mylife_mk3.adapters.LocationRecordsAdapter;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class LocationDayFragment extends Fragment {


    private String getDayRecordsURL = "http://10.0.2.2:8080/api/getLocationsDay";
    View viewReference;
    RequestQueue queue;
    private SharedPreferences sharedPreferences;
    private LayoutInflater myInflater;
    private Bundle savedInstanceState;

    ListView listView;


    public LocationDayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_location_day, container, false);

        viewReference = rootView;
        myInflater = inflater;
        sharedPreferences  = getActivity().getSharedPreferences(getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);
        listView  = (ListView) viewReference.findViewById(R.id.locationDay_listView);
        queue = Volley.newRequestQueue(getActivity());
        this.savedInstanceState = savedInstanceState;

        DayScrollDatePicker picker = (DayScrollDatePicker) rootView.findViewById(R.id.locationDay_datepicker);
        picker.setStartDate(1, 3, 2017);
        picker.setEndDate(31, 12, 2100);

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

    private void dateSelected(Date selectedDate){
        final ProgressBar progressBar = (ProgressBar) viewReference.findViewById(R.id.locationDay_progress);
        final TextView noRecords = (TextView) viewReference.findViewById(R.id.locationDay_noRecords);

        noRecords.setVisibility(TextView.GONE);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        listView.setVisibility(ListView.GONE);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(selectedDate);

        Map<String, String> params = new HashMap<String, String>();
        params.put("userID", sharedPreferences.getString("databaseID", ""));
        params.put("date", date);
        params.put("token", sharedPreferences.getString("token", ""));

        JSONObject object = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getDayRecordsURL, object, new Response.Listener<JSONObject>() {
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

                            ArrayList<LocationRecordModel> records = new ArrayList<LocationRecordModel>();
                            String id;
                            String latitude;
                            String longitude;
                            String provider;
                            String date;
                            String time;
                            JSONObject temp;
                            String address = "";
                            String finalAddress = "Unknown Location";
                            List<Address> addresses = null;
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                            for (int i = 0; i<results.length(); ++i){
                                id = ((JSONObject)results.get(i)).getString("_id");
                                latitude = ((JSONObject)results.get(i)).getString("updated_lat");
                                longitude = ((JSONObject)results.get(i)).getString("updated_long");
                                provider = ((JSONObject)results.get(i)).getString("provider");
                                temp = ((JSONObject)results.get(i)).getJSONObject("updated_at");
                                date = temp.getString("year") + "-" + temp.getString("month") + "-" + temp.getString("day");
                                time = temp.getString("time") + " " + temp.getString("offset");

                                try {
                                    addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                                    if (!addresses.isEmpty()){
                                        Address object = addresses.get(0);
                                        for (int j = 0; j<object.getMaxAddressLineIndex();++j){
                                            if(j != object.getMaxAddressLineIndex()-1){
                                                address = address + object.getAddressLine(j) + ", ";
                                            }
                                            else{
                                                address = address + object.getAddressLine(j);
                                            }

                                        }

                                        finalAddress = address + ", " + object.getCountryName();
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                LocationRecordModel model = new LocationRecordModel(id, latitude, longitude, provider, date, time, finalAddress);
                                records.add(model);
                                address = "";
                                finalAddress = "Unknown Location";
                            }

                            LocationRecordsAdapter adapter = new LocationRecordsAdapter(LocationDayFragment.this.getActivity(), records, savedInstanceState);

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
