package com.example.mylife_mk3.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.mylife_mk3.R;
import com.example.mylife_mk3.activities.DayCalendarViewActivity;


public class MyDayFragment extends android.app.Fragment {

    public MyDayFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_day, container, false);

        RelativeLayout calendarView = (RelativeLayout) rootView.findViewById(R.id.day_calendar_view);
        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DayCalendarViewActivity.class);
                getActivity().startActivity(intent);
            }
        });

//        RelativeLayout timelineView = (RelativeLayout) rootView.findViewById(R.id.day_timeline_view);
//        timelineView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(getActivity(), DayCalendarViewActivity.class);
////                getActivity().startActivity(intent);
//            }
//        });

        return rootView;
    }
}
