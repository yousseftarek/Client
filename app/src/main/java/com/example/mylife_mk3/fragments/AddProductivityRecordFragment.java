package com.example.mylife_mk3.fragments;


import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mylife_mk3.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddProductivityRecordFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener{

    private String addRecordURL = "http://10.0.2.2:8080/api/addProductivityRecord";
    private SharedPreferences sharedPreferences;
    RequestQueue queue;

    private String dateChosen;
    private String timeChosen;
    private String appName;
    private boolean productive;
    private String activity;
    private String durationString;
    private String category;
    private boolean isDuration;

    View viewReference;

    public AddProductivityRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_productivity_record, container, false);
        viewReference = rootView;

        queue = Volley.newRequestQueue(getContext());
        sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.preference_file_key), getContext().MODE_PRIVATE);

        Button date = (Button) rootView.findViewById(R.id.add_productivity_date_button);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddProductivityRecordFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });

        Spinner spinner = (Spinner) rootView.findViewById(R.id.add_productivity_category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Button time = (Button) rootView.findViewById(R.id.add_productivity_time_button);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDuration(false);
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(AddProductivityRecordFragment.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
            }
        });
        Button duration = (Button) rootView.findViewById(R.id.add_productivity_duration_button);
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDuration(true);
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(AddProductivityRecordFragment.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
            }
        });


        final RadioGroup group = (RadioGroup) rootView.findViewById(R.id.add_productivity_radioGroup);
        group.check(R.id.add_productivity_productive);

        Button submit = (Button) rootView.findViewById(R.id.add_productivity_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText app = (EditText) rootView.findViewById(R.id.add_productivity_source_edit);
                Button date = (Button) rootView.findViewById(R.id.add_productivity_date_button);
                Button time = (Button) rootView.findViewById(R.id.add_productivity_time_button);
                Button duration = (Button) rootView.findViewById(R.id.add_productivity_duration_button);
                EditText activity = (EditText) rootView.findViewById(R.id.add_productivity_activity);

                boolean temp;

                if(group.getCheckedRadioButtonId() == R.id.add_productivity_productive){
                    temp = true;
                }
                else{
                    temp = false;
                }

                addRecordInDB(date.getText().toString(), time.getText().toString(),
                        app.getText().toString(), temp, activity.getText().toString(), duration.getText().toString(), getCategory());
            }
        });

        return rootView;
    }
//
//    @Override
//    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
//
//    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Button date = (Button) viewReference.findViewById(R.id.add_productivity_date_button);
        String temp = dayOfMonth + "-" + (monthOfYear+1) + "-" + year;
        date.setText(temp);
//        dateChosen = temp;
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String temp;
        if(isDuration()){
            Button time = (Button) viewReference.findViewById(R.id.add_productivity_duration_button);
            temp = hourOfDay + ":" + minute + ":00";
            time.setText(temp);
        }
        else{
            Button time = (Button) viewReference.findViewById(R.id.add_productivity_time_button);
            temp = hourOfDay + ":" + minute + ":00";
            time.setText(temp);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        category = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public boolean isDuration() {
        return isDuration;
    }

    public void setDuration(boolean duration) {
        isDuration = duration;
    }

    private void addRecordInDB(String date, String time, String appName, boolean productive,
                               String activity, String duration, String category){

//        var record = {
//                userID: req.body.userId,
//                date: req.body.date,
//                time: req.body.time,
//                duration: req.body.duration,
//                productive: productive,
//                activity: req.body.activity,
//                source: req.body.source
//  }

        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sharedPreferences.getString("databaseID", ""));
        params.put("date", date);
        params.put("time", time);
        params.put("duration", duration);
        params.put("productive", productive + "");
        params.put("activity", activity);
        params.put("source", appName);
        params.put("category", category);
        params.put("token", sharedPreferences.getString("token", ""));

        JSONObject object = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addRecordURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestSuccess = response.getBoolean("success");
                    if (requestSuccess){
                        Toast.makeText(getContext(), "Record Added!", Toast.LENGTH_SHORT).show();
                        clearView();
                    }
                    else{
                        Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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

    public String getCategory(){
        return  category;
    }

    private void clearView(){
        EditText app = (EditText) viewReference.findViewById(R.id.add_productivity_source_edit);
        Button date = (Button) viewReference.findViewById(R.id.add_productivity_date_button);
        Button time = (Button) viewReference.findViewById(R.id.add_productivity_time_button);
        Button duration = (Button) viewReference.findViewById(R.id.add_productivity_duration_button);
        EditText activity = (EditText) viewReference.findViewById(R.id.add_productivity_activity);
        RadioGroup group = (RadioGroup) viewReference.findViewById(R.id.add_productivity_radioGroup);


        app.setText("");
        date.setText("1-1-1990");
        time.setText("00:00:00");
        duration.setText("00:00:00");
        activity.setText("");
        group.clearCheck();
    }
}
