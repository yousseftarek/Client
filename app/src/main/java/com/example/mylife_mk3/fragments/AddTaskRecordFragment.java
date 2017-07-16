package com.example.mylife_mk3.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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


public class AddTaskRecordFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{


    private String addRecordURL = "http://10.0.2.2:8080/api/addTask";
    private SharedPreferences sharedPreferences;
    RequestQueue queue;

    private View viewReference;
    private boolean isDuration;

    public AddTaskRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_add_task_record, container, false);
        viewReference = rootView;

        queue = Volley.newRequestQueue(getContext());
        sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.preference_file_key), getContext().MODE_PRIVATE);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

        Button date = (Button) rootView.findViewById(R.id.add_task_date_button);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddTaskRecordFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });

        Button time = (Button) rootView.findViewById(R.id.add_task_time_button);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDuration = false;
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddTaskRecordFragment.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND), true);
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
            }
        });

        Button duration = (Button) rootView.findViewById(R.id.add_task_duration_button);
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDuration = true;
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddTaskRecordFragment.this
                        , now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND), true);
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
            }
        });

        Button submit = (Button) rootView.findViewById(R.id.add_task_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button date = (Button) rootView.findViewById(R.id.add_task_date_button);
                Button time = (Button) rootView.findViewById(R.id.add_task_time_button);
                Button duration = (Button) rootView.findViewById(R.id.add_task_duration_button);
                EditText title = (EditText) rootView.findViewById(R.id.add_task_title_edit);
                EditText description = (EditText) rootView.findViewById(R.id.add_task_description_edit);

                addRecordInDB(title.getText().toString(), description.getText().toString(), date.getText().toString(), time.getText().toString(), duration.getText().toString());
            }
        });

        return rootView;
    }

    private void addRecordInDB(String title, String description, String date, String time, String duration) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userID", sharedPreferences.getString("databaseID", ""));
        params.put("date", date);
        params.put("time", time);
        params.put("duration", duration);
        params.put("title", title);
        params.put("description", description);
        params.put("token", sharedPreferences.getString("token", ""));

        JSONObject object = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addRecordURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestSuccess = response.getBoolean("success");
                    if (requestSuccess){
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
        Button date = (Button) viewReference.findViewById(R.id.add_task_date_button);
        String temp = dayOfMonth + "-" + (monthOfYear+1) + "-" + year;
        date.setText(temp);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String temp;
        if(isDuration){
            Button time = (Button) viewReference.findViewById(R.id.add_task_duration_button);
            temp = hourOfDay + ":" + minute + ":00";
            time.setText(temp);
        }
        else{
            Button time = (Button) viewReference.findViewById(R.id.add_task_time_button);
            temp = hourOfDay + ":" + minute + ":00";
            time.setText(temp);
        }
    }
}
