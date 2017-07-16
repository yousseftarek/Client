package com.example.mylife_mk3.fragments;



import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.mylife_mk3.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;


public class AddLocationRecordFragment extends Fragment {

    MapView mapView;
    final ArrayList<Marker> markers = new ArrayList<Marker>();
    private String addURL = "http://10.0.2.2:8080/api/addLocationRecord";
    RequestQueue queue;
    private SharedPreferences sharedPreferences;

    public AddLocationRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_location_record, container, false);
        queue = Volley.newRequestQueue(getContext());
        sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.preference_file_key), getContext().MODE_PRIVATE);

//        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
//
//
//
//        Log.d("FragmentSize", fm.getFragments().get(2).getChildFragmentManager().getFragments().get(0).getClass()+"");
//
//        mapFragment =
//                (SupportMapFragment) fm.getFragments().get(2).getChildFragmentManager().findFragmentById(R.id.add_location_map);
//        mapFragment.getMapAsync(this);

        final Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        mapView = (MapView) rootView.findViewById(R.id.add_location_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                LatLng position = new LatLng(29.9867, 31.4387);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        String address = "";
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            Address object = addresses.get(0);
                            for (int i = 0; i<object.getMaxAddressLineIndex();++i){
                                if(i != object.getMaxAddressLineIndex()-1){
                                    address = address + object.getAddressLine(i) + ", ";
                                }
                                else{
                                    address = address + object.getAddressLine(i);
                                }

                            }

                            final String temp = address + ", " + object.getCountryName();

                            Marker m = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(false).title(temp));
                            markers.add(m);

                            showDialog(temp, m, markers);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        return rootView;
    }


    public void showDialog(String address, final Marker marker, final ArrayList<Marker> markers){
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title("Add New Record")
                .customView(R.layout.fragment_add_location_details, true)
                .positiveText("Create")
                .negativeText("Cancel")
                .show();
        final View view = dialog.getCustomView();
        TextView addr = (TextView) view.findViewById(R.id.add_location_address);
        addr.setText(address);

        MDButton update = (MDButton)dialog.getActionButton(DialogAction.POSITIVE);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialNumberPicker day = (MaterialNumberPicker) view.findViewById(R.id.add_location_picker_day);
                MaterialNumberPicker month = (MaterialNumberPicker) view.findViewById(R.id.add_location_picker_month);
                MaterialNumberPicker year = (MaterialNumberPicker) view.findViewById(R.id.add_location_picker_year);
                MaterialNumberPicker hour = (MaterialNumberPicker) view.findViewById(R.id.add_location_picker_hour);
                MaterialNumberPicker minute = (MaterialNumberPicker) view.findViewById(R.id.add_location_picker_minute);
                MaterialNumberPicker second = (MaterialNumberPicker) view.findViewById(R.id.add_location_picker_second);


                Toast.makeText(getContext(), "Making Request", Toast.LENGTH_SHORT).show();
                String d = (day.getValue()<10) ? "0" + day.getValue() : day.getValue() + "";
                String m = (month.getValue()<10) ? "0" + month.getValue() : month.getValue() + "";
                String date = year.getValue() + "-" + m + "-" + d;

                String h = (hour.getValue() < 10) ? "0" + hour.getValue() : "" + hour.getValue();
                String min = (minute.getValue() < 10) ? "0" + minute.getValue() : "" + minute.getValue();
                String sec = (second.getValue() < 10) ? "0" + second.getValue() : "" + second.getValue();
                String time = h + ":" + min + ":" + sec;

                saveRecordInDB(marker.getPosition().latitude + "", marker.getPosition().longitude + "", date, time);
                dialog.dismiss();
            }
        });

        MDButton cancel = (MDButton)dialog.getActionButton(DialogAction.NEGATIVE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(markers.size() > 0){
                    marker.remove();
                    markers.remove(markers.size()-1);
                    dialog.dismiss();
                }

            }
        });
    }

    private void saveRecordInDB(String latitude, String longitude, String date, String time) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("date", date);
        params.put("time", time);
        params.put("userID", sharedPreferences.getString("databaseID", ""));
        params.put("token", sharedPreferences.getString("token", ""));

        JSONObject object = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestSuccess = response.getBoolean("success");
                    if(requestSuccess){
                        Toast.makeText(getContext(), "Record Added!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
