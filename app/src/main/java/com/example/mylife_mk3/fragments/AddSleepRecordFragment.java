package com.example.mylife_mk3.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mylife_mk3.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;


public class AddSleepRecordFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private String addURL = "http://10.0.2.2:8080/api/addSleepRecord";
    private RequestQueue queue;
    private SharedPreferences sharedPreferences;
    private boolean startT;
    private boolean startD;
    private String startTime, endTime, startDate, endDate;
    private Button startTimeButton, endTimeButton, startDateButton, endDateButton;

    public AddSleepRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_add_sleep_record, container, false);
        queue = Volley.newRequestQueue(getContext());
        sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.preference_file_key), getContext().MODE_PRIVATE);


//        final MaterialNumberPicker startDay = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_start_day);
//        final MaterialNumberPicker startMonth = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_start_month);
//        final MaterialNumberPicker startYear = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_start_year);
//        final MaterialNumberPicker startHour = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_start_hour);
//        final MaterialNumberPicker startMinute = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_start_minute);
//        final MaterialNumberPicker startSecond = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_start_second);

//        final MaterialNumberPicker endDay = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_end_day);
//        final MaterialNumberPicker endMonth = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_end_month);
//        final MaterialNumberPicker endYear = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_end_year);
//        final MaterialNumberPicker endHour = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_end_hour);
//        final MaterialNumberPicker endMinute = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_end_minute);
//        final MaterialNumberPicker endSecond = (MaterialNumberPicker) rootView.findViewById(R.id.add_sleep_picker_end_second);

        final Calendar now = Calendar.getInstance();

        startTimeButton = (Button) rootView.findViewById(R.id.add_sleep_startTime);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startT = true;

                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddSleepRecordFragment.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND), true);
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
            }
        });

        endTimeButton = (Button) rootView.findViewById(R.id.add_sleep_endTime);
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startT = false;

                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddSleepRecordFragment.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND), true);
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
            }
        });

        startDateButton = (Button) rootView.findViewById(R.id.add_sleep_startDate);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startD = true;

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddSleepRecordFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });

        endDateButton = (Button) rootView.findViewById(R.id.add_sleep_endDate);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startD = false;

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddSleepRecordFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });

        Button submit = (Button) rootView.findViewById(R.id.add_sleep_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String sTime = startHour.getValue() + ":" + startMinute.getValue() + ":00";
//                String eTime = endHour.getValue() + ":" + endMinute.getValue() + ":00";
//                String sDate = startYear.getValue() + "-" + startMonth.getValue() + "-" + startDay.getValue();
//                String eDate = endYear.getValue() + "-" + endMonth.getValue() + "-" + endDay.getValue();

                saveRecordInDB();
            }
        });

        return rootView;
    }

    private void saveRecordInDB() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("start_date", startDate);
        params.put("end_date", endDate);
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (startD){
            startDate = dayOfMonth + "-" + monthOfYear + "-" + year;
            startDateButton.setText(startDate);
        }
        else {
            endDate = dayOfMonth + "-" + monthOfYear + "-" + year;
            endDateButton.setText(endDate);
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if(startT){
            startTime = hourOfDay + ":" + minute + ":" + second;
            startTimeButton.setText(startTime);
        }
        else {
            endTime = hourOfDay + ":" + minute + ":" + second;
            endTimeButton.setText(endTime);
        }
    }
}
