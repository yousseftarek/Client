package com.example.mylife_mk3.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mylife_mk3.models.DayRecordModel;
import com.example.mylife_mk3.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DayRecordsAdapter extends ArrayAdapter<DayRecordModel> {

    private Context context;

    public DayRecordsAdapter (Context context, ArrayList<DayRecordModel> records){
        super(context, 0, records);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        final DayRecordModel record = getItem(position);

        if(convertView == null){
//            switch(record.type){ //change the layout rendered based on the record type.
//                case "SLEEP":
//                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_day_sleep_record, parent, false);
//            }

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_day_record, parent, false);
        }

        TextView time = (TextView) convertView.findViewById(R.id.day_record_time);
        TextView appName = (TextView) convertView.findViewById(R.id.day_record_app_text);
        TextView appDuration = (TextView) convertView.findViewById(R.id.day_record_app_time);
        TextView sleepFrom = (TextView) convertView.findViewById(R.id.day_record_sleep_text_from);
        TextView sleepTo = (TextView) convertView.findViewById(R.id.day_record_sleep_text_to);
        TextView sleepToTime = (TextView) convertView.findViewById(R.id.day_record_sleep_time_to);
        TextView location = (TextView) convertView.findViewById(R.id.day_record_location_text);
        TextView positionText = (TextView) convertView.findViewById(R.id.day_record_position_text);
        TextView mealText = (TextView) convertView.findViewById(R.id.day_record_meal_text);
        TextView task = (TextView) convertView.findViewById(R.id.day_record_task_text);
        TextView taskDuration = (TextView) convertView.findViewById(R.id.day_record_task_duration);

        switch (record.type){
            case "SLEEP" :
                try {
                    String startTime = record.startTime.getInt("hour") + ":" + record.startTime.getInt("minute") + ":" + record.startTime.getInt("second");
                    String endTime = record.endTime.getInt("hour") + ":" + record.endTime.getInt("minute") + ":" + record.endTime.getInt("second");
                    time.setText(startTime);
                    sleepFrom.setVisibility(TextView.VISIBLE);
                    sleepTo.setVisibility(TextView.VISIBLE);
                    sleepToTime.setText(endTime);
                    sleepToTime.setVisibility(TextView.VISIBLE);

                    appDuration.setVisibility(TextView.GONE);
                    appName.setVisibility(TextView.GONE);
                    location.setVisibility(TextView.GONE);
                    positionText.setVisibility(TextView.GONE);
                    mealText.setVisibility(TextView.GONE);
                    task.setVisibility(TextView.GONE);
                    taskDuration.setVisibility(TextView.GONE);
                    convertView.findViewById(R.id.day_record_layout).setBackgroundTintList(convertView.getResources().getColorStateList(R.color.sleep));
                    break;
                } catch (JSONException e) {
                    e.printStackTrace();
                    break;
                }
            case "PRODUCTIVITY":
                try {
                    String startTime = record.startTime.getInt("hour") + ":" + record.startTime.getInt("minute") + ":" + record.startTime.getInt("second");
                    appName.setText("Using " + record.appName);
                    appDuration.setText("For: " + record.duration);
                    appName.setVisibility(TextView.VISIBLE);
                    appDuration.setVisibility(TextView.VISIBLE);
                    time.setText(startTime);
                    sleepFrom.setVisibility(TextView.GONE);
                    sleepTo.setVisibility(TextView.GONE);
                    sleepToTime.setVisibility(TextView.GONE);
                    location.setVisibility(TextView.GONE);
                    positionText.setVisibility(TextView.GONE);
                    mealText.setVisibility(TextView.GONE);
                    task.setVisibility(TextView.GONE);
                    taskDuration.setVisibility(TextView.GONE);
                    break;
                } catch (JSONException e) {
                    e.printStackTrace();
                    break;
                }
            case "LOCATION":
                try {
                    String startTime = record.startTime.getInt("hour") + ":" + record.startTime.getInt("minute") + ":" + record.startTime.getInt("second");
                    location.setVisibility(TextView.VISIBLE);

                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                    String address = "";
                    List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(record.location.getString("latitude")), Double.parseDouble(record.location.getString("longitude")), 1);
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

                    positionText.setText(temp);
                    positionText.setVisibility(TextView.VISIBLE);

                    time.setText(startTime);
                    sleepFrom.setVisibility(TextView.GONE);
                    sleepTo.setVisibility(TextView.GONE);
                    sleepToTime.setVisibility(TextView.GONE);

                    appName.setVisibility(TextView.GONE);
                    appDuration.setVisibility(TextView.GONE);
                    mealText.setVisibility(TextView.GONE);
                    task.setVisibility(TextView.GONE);
                    taskDuration.setVisibility(TextView.GONE);
                    convertView.findViewById(R.id.day_record_layout).setBackgroundTintList(convertView.getResources().getColorStateList(R.color.location));
                    break;
                } catch (JSONException e) {
                    e.printStackTrace();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            case "MEAL":
                try {
                    String startTime = record.startTime.getInt("hour") + ":" + record.startTime.getInt("minute") + ":" + record.startTime.getInt("second");
                    Log.d("KHARA", record.mealType);
                    mealText.setText("Having " + record.mealType);
                    mealText.setVisibility(TextView.VISIBLE);

                    time.setText(startTime);
                    sleepFrom.setVisibility(TextView.GONE);
                    sleepTo.setVisibility(TextView.GONE);
                    sleepToTime.setVisibility(TextView.GONE);

                    appName.setVisibility(TextView.GONE);
                    appDuration.setVisibility(TextView.GONE);
                    location.setVisibility(TextView.GONE);
                    positionText.setVisibility(TextView.GONE);
                    task.setVisibility(TextView.GONE);
                    taskDuration.setVisibility(TextView.GONE);
                    convertView.findViewById(R.id.day_record_layout).setBackgroundTintList(convertView.getResources().getColorStateList(R.color.meal));
                    break;
                } catch (JSONException e) {
                    e.printStackTrace();
                    break;
                }
            case "TASK":
                try {
                    String startTime = record.startTime.getInt("hour") + ":" + record.startTime.getInt("minute") + ":" + record.startTime.getInt("second");
                    time.setText(startTime);

                    task.setText("Working On: " + record.taskTitle);
                    taskDuration.setText("For: " + record.duration);
                    task.setVisibility(TextView.VISIBLE);
                    taskDuration.setVisibility(TextView.VISIBLE);
                    sleepFrom.setVisibility(TextView.GONE);
                    sleepTo.setVisibility(TextView.GONE);
                    sleepToTime.setVisibility(TextView.GONE);

                    appName.setVisibility(TextView.GONE);
                    appDuration.setVisibility(TextView.GONE);
                    location.setVisibility(TextView.GONE);
                    positionText.setVisibility(TextView.GONE);
                    mealText.setVisibility(TextView.GONE);
                    convertView.findViewById(R.id.day_record_layout).setBackgroundTintList(convertView.getResources().getColorStateList(R.color.mood));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

        return convertView;
    }

}
