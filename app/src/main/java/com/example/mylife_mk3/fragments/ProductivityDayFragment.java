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
import com.example.mylife_mk3.models.ProductivityRecordModel;
import com.example.mylife_mk3.R;
import com.example.mylife_mk3.adapters.ProductivityRecordsAdapter;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
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

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;


public class ProductivityDayFragment extends Fragment{

    private String getDayRecordsURL = "http://10.0.2.2:8080/api/getProductivityDay";
    private float productivePercentage = 50;
    View viewReference;
    RequestQueue queue;
    private SharedPreferences sharedPreferences;
    private LayoutInflater myInflater;

    ListView listView;
    View header;



    public ProductivityDayFragment() {
        // Required empty public constructor
    }

    public void setProductivePercentage(float x){
        this.productivePercentage = (float) x;
    }

    public void initiatePieView(){
        PieView pieView = (PieView) viewReference.findViewById(R.id.dayPieView);

        TextView tv = (TextView) viewReference.findViewById(R.id.productive_Text);


        pieView.setPercentage(getProductivePercentage());
        tv.setText("Productive");


        PieAngleAnimation animation = new PieAngleAnimation(pieView);
        animation.setDuration(1000); //This is the duration of the animation in millis
        pieView.startAnimation(animation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_productivity__day, container, false);

        viewReference = rootView;
        myInflater = inflater;
        sharedPreferences  = getActivity().getSharedPreferences(getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);
        listView  = (ListView) viewReference.findViewById(R.id.productivityDay_listView);
        header  = inflater.inflate(R.layout.productivityday_listheader, null);

        listView.addHeaderView(header);

//        HorizontalPicker picker = (HorizontalPicker) rootView.findViewById(R.id.productivityDay_datepicker);
//        picker.setListener(this).setOffset(60).init();
//
//        picker.setDate(new DateTime());

        DayScrollDatePicker picker = (DayScrollDatePicker) rootView.findViewById(R.id.productivityDay_datepicker);
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

        queue = Volley.newRequestQueue(getActivity());


        return rootView;
    }

    public float getProductivePercentage() {
        return productivePercentage;
    }

    public void dateSelected(Date dateSelected) {

        final ProgressBar bar = (ProgressBar) viewReference.findViewById(R.id.productivityDay_progress);
        final TextView norecords = (TextView) viewReference.findViewById(R.id.productivityDay_noRecords);
        final View myHeader = header;
        final ListView myList = listView;

        myHeader.findViewById(R.id.dayPieView).setVisibility(PieView.GONE);
        myHeader.findViewById(R.id.productive_Text).setVisibility(TextView.GONE);


        listView.setVisibility(ListView.GONE);

        bar.setVisibility(ProgressBar.VISIBLE);
        norecords.setVisibility(TextView.GONE);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");


        String date = format.format(dateSelected);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userID", sharedPreferences.getString("databaseID", ""));
        params.put("date", date);
        params.put("token", sharedPreferences.getString("token", ""));

        final LayoutInflater inflater = myInflater;

        JSONObject object = new JSONObject(params);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, getDayRecordsURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestSuccess = response.getBoolean("success");
                    if(requestSuccess){
                        JSONArray results = response.getJSONArray("records");
                        bar.setVisibility(ProgressBar.GONE);

                        if(results.length() == 0){
                            norecords.setVisibility(TextView.VISIBLE);
                            myList.setVisibility(ListView.GONE);
                            myHeader.findViewById(R.id.dayPieView).setVisibility(PieView.GONE);
                            myHeader.findViewById(R.id.productive_Text).setVisibility(TextView.GONE);
                        }
                        else{
                            norecords.setVisibility(TextView.GONE);
                            myHeader.findViewById(R.id.dayPieView).setVisibility(PieView.VISIBLE);
                            myHeader.findViewById(R.id.productive_Text).setVisibility(TextView.VISIBLE);

                            ArrayList<ProductivityRecordModel> records = new ArrayList<ProductivityRecordModel>();
                            String appName;
                            String duration;
                            boolean productive;
                            String activity;
                            String temp;
                            String []temp2;
                            JSONObject timeObject;
                            String startTime;

                            for (int i = 0;i<results.length(); ++i){
                                appName = ((JSONObject)results.get(i)).getString("source");
                                temp = ((JSONObject)results.get(i)).getString("duration");
                                temp2 = temp.split(":");
                                duration = temp2[0] + "h "+temp2[1] + "m";
                                productive = (((JSONObject) results.get(i)).getBoolean("updated_productive"));
                                timeObject = ((JSONObject) results.get(i)).getJSONObject("start_time");

                                startTime = timeObject.getInt("hour") + ":" + timeObject.getInt("minute") + ":" + timeObject.getInt("second") + timeObject.getString("offset");
                                activity = ((JSONObject)results.get(i)).getString("updated_activity");
                                ProductivityRecordModel model = new ProductivityRecordModel(((JSONObject)results.get(i)).getString("_id"),appName, duration, productive, "", activity, startTime);
                                records.add(model);
                            }

                            ProductivityRecordsAdapter adapter = new ProductivityRecordsAdapter(ProductivityDayFragment.this.getActivity(), records, ProductivityDayFragment.this, null);



                            //ListView listView = (ListView) viewReference.findViewById(R.id.productivityDay_listView);

                            myList.setAdapter(adapter);
                            myList.setVisibility(ListView.VISIBLE);
//                        if(records.isEmpty()){
//                            listView.setAdapter(null);
//                        }
//                        else{
//                            listView.setAdapter(adapter);
//                        }


                            JSONObject percentage = response.getJSONObject("percentage");

                            float percentageProductive = (float)percentage.getDouble("productive");

                            setProductivePercentage(percentageProductive);

                            initiatePieView();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        queue.add(jsonObjectRequest);

    }

}
