package com.example.mylife_mk3.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mylife_mk3.models.LocationRecordModel;
import com.example.mylife_mk3.R;
import com.example.mylife_mk3.activities.MyLocationsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class LocationRecordsAdapter extends ArrayAdapter<LocationRecordModel> {

    private Context context;
    private String updateURL = "http://10.0.2.2:8080/api/updateLocationRecord";
    private SharedPreferences sharedPreferences;
    private Bundle savedInstanceState;
    RequestQueue queue;

    public LocationRecordsAdapter(Context context, ArrayList<LocationRecordModel> records, Bundle savedInstanceState) {
        super(context, 0, records);

        this.context = context;
        queue = Volley.newRequestQueue(context);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), context.MODE_PRIVATE);
        this.savedInstanceState = savedInstanceState;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final LocationRecordModel record = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_location_record, parent, false);
        }

        TextView provider = (TextView) convertView.findViewById(R.id.location_record_provider);
        provider.setText(record.provider);

        TextView time = (TextView) convertView.findViewById(R.id.location_record_time);
        time.setText(record.time);


//        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//
//        try {
//            String address = "";
//            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(record.latitude), Double.parseDouble(record.longitude), 1);
//            Address object = addresses.get(0);
//            for (int i = 0; i<object.getMaxAddressLineIndex();++i){
//                if(i != object.getMaxAddressLineIndex()-1){
//                    address = address + object.getAddressLine(i) + ", ";
//                }
//                else{
//                    address = address + object.getAddressLine(i);
//                }
//
//            }
//
//            final String temp = address + ", " + object.getCountryName();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        TextView locationText = (TextView) convertView.findViewById(R.id.location_record_location_text);
        locationText.setText(record.address);
        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.location_record_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(record.id, record.latitude, record.longitude, record.address, record.provider, record.time, record.date);
            }
        });
        MapView map = (MapView) convertView.findViewById(R.id.location_record_map);

        map.onCreate(savedInstanceState);


        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                LatLng position = new LatLng(Float.parseFloat(record.latitude), Float.parseFloat(record.longitude));
                googleMap.addMarker(new MarkerOptions().position(position).title("You Were Here"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
            }
        });

        return convertView;
    }

    private void showDialog(final String id, final String latitude, final String longitude, String address, String provider, String time, String date){
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Edit Record")
                .customView(R.layout.fragment_edit_location_record, true)
                .positiveText("Update")
                .negativeText("Cancel")
                .show();

        View view = dialog.getCustomView();
        TextView addressText = (TextView) view.findViewById(R.id.edit_location_text);
        addressText.setText(address);

        TextView timeText = (TextView) view.findViewById(R.id.edit_location_time);
        timeText.setText(date + " | " + time);

        TextView providerText = (TextView) view.findViewById(R.id.edit_location_provider);
        providerText.setText(provider);

        MapView map = (MapView) view.findViewById(R.id.edit_location_map);
        map.onCreate(savedInstanceState);


        map.getMapAsync(new OnMapReadyCallback() {  //<----------- CONTINUE HERE -------------->
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                //googleMap.getUiSettings().setScrollGesturesEnabled(true); //This Doesn't have any effect on Lite maps.
                final ArrayList<Marker> markers = new ArrayList<Marker>();
                LatLng position = new LatLng(Float.parseFloat(latitude), Float.parseFloat(longitude));
                Marker temp = googleMap.addMarker(new MarkerOptions().position(position).title("You Were Here"));
                markers.add(temp);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        googleMap.clear();
                        markers.remove(0);
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("New Location"));
                        markers.add(marker);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    }
                });

                MDButton update = (MDButton)dialog.getActionButton(DialogAction.POSITIVE);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Marker marker = markers.get(0);
                        LatLng position = marker.getPosition();
                        updateRecordInDB(id, position.latitude + "", position.longitude + "");
                        dialog.dismiss();
                    }
                });

            }
        });
    }

    private void updateRecordInDB(String id, String latitude, String longitude){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = format.format(c.getTime());

        String[] updateTime = dateTime.split(" ");

        Map<String, String> params = new HashMap<String, String>();
        params.put("recordID", id);
        params.put("date", updateTime[0]);
        params.put("time", updateTime[1]);
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("token", sharedPreferences.getString("token", ""));

        JSONObject object = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, updateURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestSuccess = response.getBoolean("success");
                    if(requestSuccess){
                        Toast.makeText(context, "Record Updated!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, MyLocationsActivity.class);
                        context.startActivity(intent);
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
