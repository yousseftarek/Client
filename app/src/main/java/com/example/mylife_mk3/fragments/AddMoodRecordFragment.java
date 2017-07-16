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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dzaitsev.android.widget.RadarChartView;
import com.example.mylife_mk3.R;
import com.gc.materialdesign.views.Slider;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.Entry;
import com.ultramegasoft.radarchart.RadarEditWidget;
import com.ultramegasoft.radarchart.RadarHolder;
import com.ultramegasoft.radarchart.RadarView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.graphics.Paint.Style.FILL;


public class AddMoodRecordFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private View viewReference;
    private String addRecordURL = "http://10.0.2.2:8080/api/addMoodRecord";
    private SharedPreferences sharedPreferences;
    RequestQueue queue;
    public AddMoodRecordFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_add_mood_record, container, false);
        viewReference = rootView;

        queue = Volley.newRequestQueue(getContext());
        sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.preference_file_key), getContext().MODE_PRIVATE);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

        final Slider happiness = (Slider) rootView.findViewById(R.id.add_mood_happy_slider);
        happiness.setValue(5);
        final Slider stress = (Slider) rootView.findViewById(R.id.add_mood_stress_slider);

        final Slider pain = (Slider) rootView.findViewById(R.id.add_mood_pain_slider);

        final Slider fear = (Slider) rootView.findViewById(R.id.add_mood_fear_slider);

        final Slider anger = (Slider) rootView.findViewById(R.id.add_mood_anger_slider);

        final Slider energy = (Slider) rootView.findViewById(R.id.add_mood_energy_slider);
        energy.setValue(5);

        Calendar c = Calendar.getInstance();

        final Button date = (Button) rootView.findViewById(R.id.add_mood_date);
        date.setText(dateFormat.format(c.getTime()));
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddMoodRecordFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });

        final Button time = (Button) rootView.findViewById(R.id.add_mood_time);
        time.setText(timeFormat.format(c.getTime()));
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddMoodRecordFragment.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND), true);
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
            }
        });

        Button submit = (Button) rootView.findViewById(R.id.add_mood_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Integer> params = new HashMap<String, Integer>();
                params.put("happiness",happiness.getValue());
                params.put("stress", stress.getValue());
                params.put("pain", pain.getValue());
                params.put("fear", fear.getValue());
                params.put("anger", anger.getValue());
                params.put("energy", energy.getValue());
                JSONObject state = new JSONObject(params);

                addRecordInDB(date.getText().toString(), time.getText().toString(), state);
            }
        });

        return rootView;
    }

    private void addRecordInDB(String date, String time, JSONObject state) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sharedPreferences.getString("databaseID", ""));
        params.put("date", date);
        params.put("time", time);
        params.put("token", sharedPreferences.getString("token", ""));
        JSONObject object = new JSONObject(params);
        try {
            object.put("mood", state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, addRecordURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestSuccess = response.getBoolean("success");
                    if(requestSuccess){
                        Toast.makeText(AddMoodRecordFragment.this.getContext(), "Record Added!", Toast.LENGTH_SHORT).show();
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
        queue.add(request);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String month = (monthOfYear < 10) ? "0" + monthOfYear : "" + monthOfYear;
        String day = (dayOfMonth < 10) ? "0" + dayOfMonth : "" + dayOfMonth;

        Button date = (Button) viewReference.findViewById(R.id.add_mood_date);
        date.setText(year + "-" + month + "-" + day);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        String hour = (hourOfDay < 10) ? "0" + hourOfDay : "" + hourOfDay;
        String min = (minute < 10) ? "0" + minute : "" + minute;
        String sec = (second < 10) ? "0" + second : "" + second;

        Button time = (Button) viewReference.findViewById(R.id.add_mood_time);
        time.setText(hour + ":" + min + ":" + sec);
    }
}
