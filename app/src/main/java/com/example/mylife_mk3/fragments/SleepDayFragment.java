package com.example.mylife_mk3.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mylife_mk3.R;


public class SleepDayFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep__day, container, false);
    }



//                            JSONObject recordStart = results.getJSONObject(0);
//                            id= recordStart.getString("_id");
//                            time = recordStart.getJSONObject("start_time");
//                            date = recordStart.getJSONObject("start_date");
//                            startDate = date.getInt("day") + "-" + date.getInt("month") + "-" + date.getInt("year");
//                            startTime = time.getInt("hour") + ":" + time.getInt("minute") + ":" + time.getInt("second");
//                            JSONObject recordEnd = results.getJSONObject(results.length()-1);
//                            time = recordEnd.getJSONObject("end_time");
//                            date = recordEnd.getJSONObject("end_date");
//                            endDate = date.getInt("day") + "-" + date.getInt("month") + "-" + date.getInt("year");
//                            endTime = time.getInt("hour") + ":" + time.getInt("minute") + ":" + time.getInt("second");
//                            SleepRecordModel model = new SleepRecordModel(id, startTime, endTime, startDate, endDate, "");
}
