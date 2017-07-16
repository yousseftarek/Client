package com.example.mylife_mk3.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.example.mylife_mk3.models.ProductivityRecordModel;
import com.example.mylife_mk3.R;
import com.example.mylife_mk3.activities.MyProductivityActivity;
import com.example.mylife_mk3.fragments.ProductivityDayFragment;
import com.example.mylife_mk3.fragments.ProductivityMonthFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProductivityRecordsAdapter extends ArrayAdapter<ProductivityRecordModel> {

    private Context context;
    private ProductivityMonthFragment monthFragment;
    private ProductivityDayFragment dayFragment;
    private String updateURL = "http://10.0.2.2:8080/api/updateProductivityRecord";
    private SharedPreferences sharedPreferences;
    RequestQueue queue;

    public ProductivityRecordsAdapter(Context context, ArrayList<ProductivityRecordModel> records, ProductivityDayFragment dayFragment, ProductivityMonthFragment monthFragment){
        super(context, 0, records);
        this.context = context;
        this.monthFragment = monthFragment;
        this.dayFragment = dayFragment;
        queue = Volley.newRequestQueue(context);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), context.MODE_PRIVATE);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final ProductivityRecordModel record = getItem(position);


        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_productivity__record, parent, false);
        }

        TextView appTitle = (TextView) convertView.findViewById(R.id.productivity_appTitle);
        TextView duration = (TextView) convertView.findViewById(R.id.productivity_duration);
        TextView productive = (TextView) convertView.findViewById(R.id.productivity_productive);
        TextView day = (TextView) convertView.findViewById(R.id.productivity_record_day_text);

        appTitle.setText(record.appName);
        duration.setText(record.duration);
        day.setText(record.dayOfTheWeek);

        if(record.productive){
            productive.setText("Productive");
            convertView.findViewById(R.id.productivityRecord_layout).setBackgroundTintList(convertView.getResources().getColorStateList(R.color.productivity_record_background_productive));
        }
        else{
            productive.setText("Not Productive");
            convertView.findViewById(R.id.productivityRecord_layout).setBackgroundTintList(convertView.getResources().getColorStateList(R.color.productivity_record_background_nonproductive));
        }

        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.productivityRecord_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ProductivityRecordsAdapter.this.getContext(), "YAY", Toast.LENGTH_SHORT).show();
                if(record.dayOfTheWeek.equals("")){
                    showEditDialog(record.id,record.appName, record.startTime, record.dayOfTheWeek + " | " + record.duration, record.activity, record.productive, true);
                }
                else {
                    showEditDialog(record.id,record.appName, record.startTime, record.dayOfTheWeek + " | " + record.duration, record.activity, record.productive, false);
                }

            }
        });

        return convertView;
    }

    private void showEditDialog(final String id, String appName, String startTime, String duration, String activity, boolean productive, boolean day){
//        android.support.v4.app.FragmentManager fm = ((MyProductivityActivity)context).getSupportFragmentManager();
//        EditProductivityRecordFragment edit = EditProductivityRecordFragment.newInstance(id,appName, startTime, duration, activity, productive);
//        if(day){
//            edit.setTargetFragment(dayFragment, 300);
//        }
//        else{
//            edit.setTargetFragment(monthFragment, 300);
//        }
//        edit.show(fm, "edit_productivity_record");

        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Edit Record")
                .customView(R.layout.fragment_edit_productivity_record, true)
                .positiveText("Update")
                .negativeText("Cancel")
                .show();

        View view= dialog.getCustomView();

        final EditText editActivity = (EditText) view.findViewById(R.id.edit_productivityRecord_activitydescription);
        editActivity.setText(activity);

        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.edit_productivityRecord_radioGroup);
        if(productive){
            radioGroup.check(R.id.edit_productivityRecord_productive);
        }
        else{
            radioGroup.check(R.id.edit_productivityRecord_notProductive);
        }

        TextView name = (TextView) view.findViewById(R.id.edit_productivityRecord_appName);
        name.setText(appName);

        TextView sTime = (TextView) view.findViewById(R.id.edit_productivityRecord_startTime);
        sTime.setText(startTime);

        TextView dur = (TextView) view.findViewById(R.id.edit_productivityRecord_duration);
        dur.setText(duration);

        MDButton update = (MDButton)dialog.getActionButton(DialogAction.POSITIVE);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioGroup.getCheckedRadioButtonId() == R.id.edit_productivityRecord_productive){
                    updateRecordInDB(id, editActivity.getText().toString(), "true");
                }
                else{
                    updateRecordInDB(id, editActivity.getText().toString(), "false");
                }
                dialog.dismiss();
            }
        });

    }

    private void updateRecordInDB(String id, String activity, String productive){

        Map<String, String> params = new HashMap<String, String>();
        params.put("recordID", id);
        params.put("updated_productive", productive);
        params.put("updated_activity", activity);
        params.put("token", sharedPreferences.getString("token", ""));

        JSONObject object = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, updateURL, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean requestSuccess = response.getBoolean("success");
                            if(requestSuccess){
                                Toast.makeText(context, "Record Updated!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context, MyProductivityActivity.class);
                                context.startActivity(intent);
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
}
