package com.example.mylife_mk3.fragments;


import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.harrywhewell.scrolldatepicker.MonthScrollDatePicker;
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


public class LocationMonthFragment extends Fragment {

    private String getMonthRecordsURL = "http://10.0.2.2:8080/api/getLocationsMonth";
    private static String getAddressFromGoogleURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=LAT,LONG&key=AIzaSyBmsujEMjcAYSQIeZJHyojiuK9pvc__yeo";

    View viewReference;
    RequestQueue queue;

    private Bundle savedInstanceState;
    private SharedPreferences sharedPreferences;
    private LayoutInflater myInflater;

    ListView listView;



    public LocationMonthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_location_month, container, false);

        viewReference = rootView;
        myInflater = inflater;
        sharedPreferences  = getActivity().getSharedPreferences(getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);
        listView  = (ListView) viewReference.findViewById(R.id.locationMonth_listView);
        queue = Volley.newRequestQueue(getActivity());
        this.savedInstanceState = savedInstanceState;

        MonthScrollDatePicker picker = (MonthScrollDatePicker) rootView.findViewById(R.id.locationMonth_datepicker);
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

    private void dateSelected(Date selectedDate){
        final ProgressBar progressBar = (ProgressBar) viewReference.findViewById(R.id.locationMonth_progress);
        final TextView noRecords = (TextView) viewReference.findViewById(R.id.locationMonth_noRecords);

        noRecords.setVisibility(TextView.GONE);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        listView.setVisibility(ListView.GONE);
        //--------------------------- Add Code to get Adapter and destroy all maps -----------------------------

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

                            ArrayList<LocationRecordModel> records = new ArrayList<LocationRecordModel>();
                            String id;
                            String latitude;
                            String longitude;
                            String provider;
                            String date;
                            String time;
                            String address = "";
                            String finalAddress = "Unknown Location";
                            JSONObject temp;
                            List<Address> addresses = null;
                            Geocoder geocoder = new Geocoder(LocationMonthFragment.this.getActivity(), Locale.getDefault());

                            for (int i = 0; i<results.length(); ++i){
                                id = ((JSONObject)results.get(i)).getString("_id");
                                latitude = ((JSONObject)results.get(i)).getString("updated_lat");
                                longitude = ((JSONObject)results.get(i)).getString("updated_long");
                                provider = ((JSONObject)results.get(i)).getString("provider");
                                temp = ((JSONObject)results.get(i)).getJSONObject("updated_at");
                                date = temp.getString("year") + "-" + temp.getString("month") + "-" + temp.getString("day");
                                time = temp.getString("time") + " " + temp.getString("offset");

                                String araf = LocationMonthFragment.this.getAddressFromGoogle(latitude, longitude);
                                try {
                                    Log.d("LOCATION", Double.parseDouble(latitude) + "-" + Double.parseDouble(longitude));

                                    addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                                    if (!address.isEmpty()){
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
                                    else{

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                LocationRecordModel model = new LocationRecordModel(id, latitude, longitude, provider, date, time, finalAddress);
                                records.add(model);
                                address = "";
                                finalAddress = "Unknown Location";
                            }

                            LocationRecordsAdapter adapter = new LocationRecordsAdapter(LocationMonthFragment.this.getActivity(), records, savedInstanceState);

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

    public String getAddressFromGoogle(String lat, String lng){
        String address = "";
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=AIzaSyDzZGbkXqKqRoKaX28YEYxkM56j6jrgOlE";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray results = response.getJSONArray("results");
                    String address = ((JSONObject)results.get(0)).getString(""); //continue here
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("GOOGLERESULT", response.toString());
//                06-05 14:41:20.571 21669-21669/com.example.mylife_mk3 D/GOOGLERESULT: {"results":[{"address_components":[{"long_name":"C Building","short_name":"C Building","types":["premise"]},{"long_name":"El-Obour Square","short_name":"El-Obour Square","types":["route"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"C Building, El-Obour Square, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":29.9872255,"lng":31.4395538},"southwest":{"lat":29.9862056,"lng":31.4383834}},"location":{"lat":29.9866504,"lng":31.438922},"location_type":"ROOFTOP","viewport":{"northeast":{"lat":29.9880645302915,"lng":31.4403175802915},"southwest":{"lat":29.9853665697085,"lng":31.4376196197085}}},"place_id":"ChIJ6_xska48WBQROklDcFX3EYA","types":["premise"]},{"address_components":[{"long_name":"Unnamed Road","short_name":"Unnamed Road","types":["route"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Unnamed Road, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":29.9873264,"lng":31.4383415},"southwest":{"lat":29.9862851,"lng":31.4381083}},"location":{"lat":29.9867955,"lng":31.4382719},"location_type":"GEOMETRIC_CENTER","viewport":{"northeast":{"lat":29.9881547302915,"lng":31.4395738802915},"southwest":{"lat":29.9854567697085,"lng":31.4368759197085}}},"place_id":"ChIJeUP06648WBQRBA2jmPPaSvs","types":["route"]},{"address_components":[{"long_name":"New Cairo City","short_name":"New Cairo City","types":["locality","political"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"New Cairo City, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.1022173,"lng":31.6126441},"southwest":{"lat":29.933515,"lng":31.362276}},"location":{"lat":30.007413,"lng":31.4913182},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.1022173,"lng":31.6126441},"southwest":{"lat":29.933515,"lng":31.362276}}},"place_id":"ChIJ53DS_M8iWBQR2J-Ih9Zziwk","types":["locality","political"]},{"address_components":[{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.486449,"lng":31.9394023},"southwest":{"lat":29.7600961,"lng":31.22601239999999}},"location":{"lat":30.1197986,"lng":31.5370003},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.486449,"lng":31.9394023},"southwest":{"lat":29.7600961,"lng":31.22601239999999}}},"place_id":"ChIJz6gXkZ0QWBQRuaJt4gI9myY","types":["administrative_area_level_1","political"]},{"address_components":[{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Egypt","geometry":{"bounds":{"northeast":{"lat":31.8122,"lng":37.0569},"southwest":{"lat":21.9999999,"lng":24.696775}},"location":{"lat":26.820553,"lng":30.802498},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":31.670987,"lng":36.8945446},"southwest":{"lat":21.9999999,"lng":24.696775}}},"place_id":"ChIJ6TZcw3aJNhQRRMTEJQmgRSw","types":["country","political"]}],"status":"OK"}
//                06-05 14:41:20.577 21669-21669/com.example.mylife_mk3 D/GOOGLERESULT: {"results":[{"address_components":[{"long_name":"Cairo - Al Wosta","short_name":"Cairo - Al Wosta","types":["route"]},{"long_name":"Qasr Ad Dobarah","short_name":"Qasr Ad Dobarah","types":["administrative_area_level_3","political"]},{"long_name":"Qasr an Nile","short_name":"Qasr an Nile","types":["administrative_area_level_2","political"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Cairo - Al Wosta, Qasr Ad Dobarah, Qasr an Nile, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.0410176,"lng":31.2312639},"southwest":{"lat":30.0384593,"lng":31.2299939}},"location":{"lat":30.0397443,"lng":31.2306138},"location_type":"GEOMETRIC_CENTER","viewport":{"northeast":{"lat":30.0410874302915,"lng":31.2319778802915},"southwest":{"lat":30.0383894697085,"lng":31.2292799197085}}},"place_id":"ChIJmVtV1s1AWBQReJjEQicqH5k","types":["route"]},{"address_components":[{"long_name":"Cairo","short_name":"Cairo","types":["locality","political"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Cairo, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.1106024,"lng":31.3019729},"southwest":{"lat":30.0083745,"lng":31.2149558}},"location":{"lat":30.0444196,"lng":31.2357116},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.1106024,"lng":31.3019729},"southwest":{"lat":30.0083745,"lng":31.2149558}}},"place_id":"ChIJ674hC6Y_WBQRujtC6Jay33k","types":["locality","political"]},{"address_components":[{"long_name":"Qasr Ad Dobarah","short_name":"Qasr Ad Dobarah","types":["administrative_area_level_3","political"]},{"long_name":"Qasr an Nile","short_name":"Qasr an Nile","types":["administrative_area_level_2","political"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Qasr Ad Dobarah, Qasr an Nile, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.0444601,"lng":31.2361835},"southwest":{"lat":30.0384515,"lng":31.2294177}},"location":{"lat":30.0411203,"lng":31.2333044},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.0444601,"lng":31.2361835},"southwest":{"lat":30.0384515,"lng":31.2294177}}},"place_id":"ChIJh9VR3M5AWBQRMuWu2iSDBwg","types":["administrative_area_level_3","political"]},{"address_components":[{"long_name":"Qasr an Nile","short_name":"Qasr an Nile","types":["administrative_area_level_2","political"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Qasr an Nile, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.0538955,"lng":31.2417666},"southwest":{"lat":30.0307055,"lng":31.228575}},"location":{"lat":30.0477057,"lng":31.2354509},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.0538955,"lng":31.2417666},"southwest":{"lat":30.0307055,"lng":31.228575}}},"place_id":"ChIJxZby9sBAWBQRGgqaOVSc74Q","types":["administrative_area_level_2","political"]},{"address_components":[{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.486449,"lng":31.9394023},"southwest":{"lat":29.7600961,"lng":31.22601239999999}},"location":{"lat":30.1197986,"lng":31.5370003},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.486449,"lng":31.9394023},"southwest":{"lat":29.7600961,"lng":31.22601239999999}}},"place_id":"ChIJz6gXkZ
//                06-05 14:41:20.578 21669-21669/com.example.mylife_mk3 D/GOOGLERESULT: {"results":[{"address_components":[{"long_name":"South Teseen","short_name":"S Teseen","types":["route"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"S Teseen, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.0209556,"lng":31.450873},"southwest":{"lat":30.0166679,"lng":31.4457401}},"location":{"lat":30.0187204,"lng":31.448396},"location_type":"GEOMETRIC_CENTER","viewport":{"northeast":{"lat":30.0209556,"lng":31.450873},"southwest":{"lat":30.0166679,"lng":31.4457401}}},"place_id":"ChIJ04sqgCojWBQRwNZXqCgIw_Q","types":["route"]},{"address_components":[{"long_name":"New Cairo City","short_name":"New Cairo City","types":["locality","political"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"New Cairo City, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.1022173,"lng":31.6126441},"southwest":{"lat":29.933515,"lng":31.362276}},"location":{"lat":30.007413,"lng":31.4913182},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.1022173,"lng":31.6126441},"southwest":{"lat":29.933515,"lng":31.362276}}},"place_id":"ChIJ53DS_M8iWBQR2J-Ih9Zziwk","types":["locality","political"]},{"address_components":[{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.486449,"lng":31.9394023},"southwest":{"lat":29.7600961,"lng":31.22601239999999}},"location":{"lat":30.1197986,"lng":31.5370003},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.486449,"lng":31.9394023},"southwest":{"lat":29.7600961,"lng":31.22601239999999}}},"place_id":"ChIJz6gXkZ0QWBQRuaJt4gI9myY","types":["administrative_area_level_1","political"]},{"address_components":[{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Egypt","geometry":{"bounds":{"northeast":{"lat":31.8122,"lng":37.0569},"southwest":{"lat":21.9999999,"lng":24.696775}},"location":{"lat":26.820553,"lng":30.802498},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":31.670987,"lng":36.8945446},"southwest":{"lat":21.9999999,"lng":24.696775}}},"place_id":"ChIJ6TZcw3aJNhQRRMTEJQmgRSw","types":["country","political"]}],"status":"OK"}
//                06-05 14:41:20.580 21669-21669/com.example.mylife_mk3 D/GOOGLERESULT: {"results":[{"address_components":[{"long_name":"Al Shabab","short_name":"Al Shabab","types":["route"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Al Shabab, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":29.9843774,"lng":31.4401442},"southwest":{"lat":29.9843522,"lng":31.4379756}},"location":{"lat":29.9843648,"lng":31.4390599},"location_type":"GEOMETRIC_CENTER","viewport":{"northeast":{"lat":29.9857137802915,"lng":31.4404088802915},"southwest":{"lat":29.98301581970851,"lng":31.4377109197085}}},"place_id":"ChIJbwp0T6k8WBQRvHN13o2UL0s","types":["route"]},{"address_components":[{"long_name":"New Cairo City","short_name":"New Cairo City","types":["locality","political"]},{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"New Cairo City, Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.1022173,"lng":31.6126441},"southwest":{"lat":29.933515,"lng":31.362276}},"location":{"lat":30.007413,"lng":31.4913182},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.1022173,"lng":31.6126441},"southwest":{"lat":29.933515,"lng":31.362276}}},"place_id":"ChIJ53DS_M8iWBQR2J-Ih9Zziwk","types":["locality","political"]},{"address_components":[{"long_name":"Cairo Governorate","short_name":"Cairo Governorate","types":["administrative_area_level_1","political"]},{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Cairo Governorate, Egypt","geometry":{"bounds":{"northeast":{"lat":30.486449,"lng":31.9394023},"southwest":{"lat":29.7600961,"lng":31.22601239999999}},"location":{"lat":30.1197986,"lng":31.5370003},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.486449,"lng":31.9394023},"southwest":{"lat":29.7600961,"lng":31.22601239999999}}},"place_id":"ChIJz6gXkZ0QWBQRuaJt4gI9myY","types":["administrative_area_level_1","political"]},{"address_components":[{"long_name":"Egypt","short_name":"EG","types":["country","political"]}],"formatted_address":"Egypt","geometry":{"bounds":{"northeast":{"lat":31.8122,"lng":37.0569},"southwest":{"lat":21.9999999,"lng":24.696775}},"location":{"lat":26.820553,"lng":30.802498},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":31.670987,"lng":36.8945446},"southwest":{"lat":21.9999999,"lng":24.696775}}},"place_id":"ChIJ6TZcw3aJNhQRRMTEJQmgRSw","types":["country","political"]}],"status":"OK"}

                }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
        return address;
    }

}
