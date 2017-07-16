package com.example.mylife_mk3.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mylife_mk3.R;
import com.example.mylife_mk3.models.SleepRecordModel;

import java.util.ArrayList;



public class SleepRecordsAdapter extends ArrayAdapter<SleepRecordModel> {

    private Context context;
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public SleepRecordsAdapter(Context context, ArrayList<SleepRecordModel> records){
        super(context, 0, records);

        this.context = context;
        queue = Volley.newRequestQueue(context);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), context.MODE_PRIVATE);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final SleepRecordModel record = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_sleep_month_record, parent, false);
        }

        TextView startDate = (TextView) convertView.findViewById(R.id.sleep_record_startDate);
        startDate.setText(record.startDate);

        TextView endDate = (TextView) convertView.findViewById(R.id.sleep_record_endDate);
        endDate.setText(record.endDate);

        TextView startTime = (TextView) convertView.findViewById(R.id.sleep_record_startTime);
        startTime.setText(record.startTime);

        TextView endTime = (TextView) convertView.findViewById(R.id.sleep_record_endTime);
        endTime.setText(record.endTime);

        TextView duration = (TextView) convertView.findViewById(R.id.sleep_record_duration);
        duration.setText(record.duration);

        return convertView;
    }
}
