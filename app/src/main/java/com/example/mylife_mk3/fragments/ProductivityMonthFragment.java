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
import com.harrywhewell.scrolldatepicker.MonthScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;


public class ProductivityMonthFragment extends Fragment {

    private String getDayRecordsURL = "http://10.0.2.2:8080/api/getProductivityMonth";
    private float productivePercentage = 50;
    View viewReference;
    RequestQueue queue;

    private SharedPreferences sharedPreferences;
    private LayoutInflater myInflater;

    ListView listView;
    View header;

    public ProductivityMonthFragment() {
        // Required empty public constructor
    }

    public void initiatePieView(){
        PieView pieView = (PieView) viewReference.findViewById(R.id.dayPieView);

        TextView tv = (TextView) viewReference.findViewById(R.id.productive_Text);



            pieView.setPercentage(getProductivePercentage());
            tv.setText("Productive");

        pieView.setVisibility(PieView.VISIBLE);
        tv.setVisibility(TextView.VISIBLE);

        PieAngleAnimation animation = new PieAngleAnimation(pieView);
        animation.setDuration(1000); //This is the duration of the animation in millis
        pieView.startAnimation(animation);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_productivity_month, container, false);

        viewReference = rootView;
        myInflater = inflater;
        sharedPreferences  = getActivity().getSharedPreferences(getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);
        listView  = (ListView) viewReference.findViewById(R.id.productivityMonth_listView);
        header  = inflater.inflate(R.layout.productivityday_listheader, null);

        listView.addHeaderView(header);

//        HorizontalPicker picker = (HorizontalPicker) rootView.findViewById(R.id.productivityMonth_datepicker);
//        picker.setListener(this).setOffset(60).init();
//
//        picker.setDate(new DateTime());

        MonthScrollDatePicker picker = (MonthScrollDatePicker) rootView.findViewById(R.id.productivityMonth_datepicker);
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

        queue = Volley.newRequestQueue(getActivity());

        return rootView;
    }

    public float getProductivePercentage() {
        return productivePercentage;
    }

    public void setProductivePercentage(float productivePercentage) {
        this.productivePercentage = productivePercentage;
    }


    public void dateSelected(Date dateSelected) {

        final ProgressBar bar = (ProgressBar) viewReference.findViewById(R.id.productivityMonth_progress);
        final TextView norecords = (TextView) viewReference.findViewById(R.id.productivityMonth_noRecords);
        final View myHeader = header;
        final ListView myList = listView;

        myHeader.findViewById(R.id.dayPieView).setVisibility(PieView.GONE);
        myHeader.findViewById(R.id.productive_Text).setVisibility(TextView.GONE);


        listView.setVisibility(ListView.GONE);

        bar.setVisibility(ProgressBar.VISIBLE);
        norecords.setVisibility(TextView.GONE);

        DateFormat format = new SimpleDateFormat("yyyy-MM");

        String date = format.format(dateSelected);

        String[] temp = date.split("-");

        Map<String, String> params = new HashMap<String, String>();
        params.put("userID", sharedPreferences.getString("databaseID", ""));
        params.put("month", temp[1]);
        params.put("year", temp[0]);
        params.put("token", sharedPreferences.getString("token", ""));

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
                                    ArrayList<ProductivityRecordModel> records = new ArrayList<ProductivityRecordModel>();
                                    String appName;
                                    String duration;
                                    boolean productive;
                                    String date;
                                    JSONObject dateObject;
                                    JSONObject timeObject;
                                    String activity;
                                    String temp;
                                    String startTime;

                                    String []temp2;
                                    int tempDay = 0;
                                    int tempMonth = 0;
                                    String temp4 = tempDay + "";
                                    String temp3 = tempMonth + "";

                                    for (int i = 0;i<results.length(); ++i){
                                        appName = ((JSONObject)results.get(i)).getString("source");
                                        activity = ((JSONObject)results.get(i)).getString("updated_activity");
                                        temp = ((JSONObject)results.get(i)).getString("duration");
                                        temp2 = temp.split(":");
                                        dateObject = ((JSONObject) results.get(i)).getJSONObject("start_date");
                                        timeObject = ((JSONObject) results.get(i)).getJSONObject("start_time");

                                        startTime = timeObject.getInt("hour") + ":" + timeObject.getInt("minute") + ":" + timeObject.getInt("second") + timeObject.getString("offset");

                                        tempMonth = dateObject.getInt("month");
                                        tempDay = dateObject.getInt("day");  //Continue HEREE -------------------------------
                                        if(tempDay < 10){
                                            temp4 = "0"+tempDay;
                                        }
                                        else{
                                            temp4 = "" + tempDay;
                                        }
                                        if(tempMonth < 10){
                                            temp3 = "0"+tempMonth;
                                        }
                                        else{
                                            temp3 = "" + tempMonth;
                                        }
                                        date = dateObject.getInt("year") + "-" + temp3 + "-" + temp4;
                                        duration = temp2[0] + "h "+temp2[1] + "m";
                                        productive = (((JSONObject) results.get(i)).getBoolean("updated_productive"));

                                        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
                                        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

                                        try {
                                            Date day = formater.parse(date);
                                            temp = sdf.format(day);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        ProductivityRecordModel model = new ProductivityRecordModel(((JSONObject)results.get(i)).getString("_id"),appName, duration, productive, temp + ", " + temp4, activity, startTime);
                                        records.add(model);
                                    }

                                    ProductivityRecordsAdapter adapter = new ProductivityRecordsAdapter(ProductivityMonthFragment.this.getActivity(), records, null, ProductivityMonthFragment.this);

                                    myList.setAdapter(adapter);
                                    myList.setVisibility(ListView.VISIBLE);

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
