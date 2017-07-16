package com.example.mylife_mk3.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mylife_mk3.R;
import com.example.mylife_mk3.activities.MyLocationsActivity;
import com.example.mylife_mk3.activities.MyMealsActivity;
import com.example.mylife_mk3.activities.MyMoodActivity;
import com.example.mylife_mk3.activities.MyProductivityActivity;
import com.example.mylife_mk3.activities.MySleepActivity;
import com.example.mylife_mk3.activities.MyTasksActivity;

import java.util.Map;



public class MyStatsFragment extends android.app.Fragment {

//    private Map<String, String> user;
//    private String serverToken;

    public MyStatsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stats_fragment, container, false);
//        Button button = (Button) rootView.findViewById(R.id.mySleep);
//        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AlexandriaFLF.ttf");
//        button.setTypeface(font);
//        button = (Button) rootView.findViewById(R.id.myLocation);
//        button.setTypeface(font);
//        button = (Button) rootView.findViewById(R.id.myProductivity);
//        button.setTypeface(font);

//        final Bundle arguments = getArguments();
//        setUser((HashMap)arguments.getSerializable("hashmap"));
//        setServerToken(arguments.getString("token"));

        Button sleep = (Button) rootView.findViewById(R.id.mySleep);
        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MySleepActivity.class);
                //intent.putExtras(arguments);
                startActivity(intent);
            }
        });

        Button locations = (Button) rootView.findViewById(R.id.myLocation);
        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyLocationsActivity.class);
                //intent.putExtras(arguments);
                startActivity(intent);
            }
        });

        Button productivity = (Button) rootView.findViewById(R.id.myProductivity);
        productivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProductivityActivity.class);
                //intent.putExtras(arguments);
                startActivity(intent);
            }
        });

        Button meals = (Button) rootView.findViewById(R.id.myMeals);
        meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyMealsActivity.class);
                //intent.putExtras(arguments);
                startActivity(intent);
            }
        });

        Button tasks = (Button) rootView.findViewById(R.id.myTasks);
        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyTasksActivity.class);
                startActivity(intent);
            }
        });

        Button mood = (Button) rootView.findViewById(R.id.myMood);
        mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyMoodActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }


}
