package com.example.mylife_mk3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mylife_mk3.R;

/**
 * Created by Tarek on 31-Mar-2017.
 */

public class EditProductivityRecordFragment extends DialogFragment {

    private EditText editActivity;
    private RadioGroup radioGroup;
    private TextView appName;
    private TextView startTime;
    private TextView duration;
    private String id;
    private Button update;
    private Button cancel;


    public EditProductivityRecordFragment(){

    }

    public interface EditProductivityRecordListener {
        void onFinishEditDialog(String activity, boolean productive, String id);
    }

    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        EditProductivityRecordListener listener = (EditProductivityRecordListener) getTargetFragment();

        boolean temp = false;

        if(radioGroup.getCheckedRadioButtonId() == R.id.edit_productivityRecord_productive){
            temp = true;
        }

        listener.onFinishEditDialog(editActivity.getText().toString(), temp, id);
        dismiss();
    }


    public static EditProductivityRecordFragment newInstance(String id, String appName, String startTime, String duration, String activity, boolean productive) {
        EditProductivityRecordFragment frag = new EditProductivityRecordFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("appName", appName);
        args.putString("startTime", startTime);
        args.putString("duration", duration);
        args.putString("activity", activity);
        args.putBoolean("productive", productive);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_productivity_record, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        editActivity = (EditText) view.findViewById(R.id.edit_productivityRecord_activitydescription);
        radioGroup = (RadioGroup) view.findViewById(R.id.edit_productivityRecord_radioGroup);
        appName = (TextView) view.findViewById(R.id.edit_productivityRecord_appName);
        startTime = (TextView) view.findViewById(R.id.edit_productivityRecord_startTime);
        duration = (TextView) view.findViewById(R.id.edit_productivityRecord_duration);
        id= getArguments().getString("id", "");
//        update = (Button) view.findViewById(R.id.edit_productivityRecord_update);
//        cancel = (Button) view.findViewById(R.id.edit_productivityRecord_cancel);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Edit Record");
        editActivity.setText(getArguments().getString("activity", ""));
        appName.setText(getArguments().getString("appName", "App Name"));
        startTime.setText(getArguments().getString("startTime", "Start Time"));
        duration.setText("Duration: "+ getArguments().getString("duration", "--h, --m"));

        if(getArguments().getBoolean("productive")){
            radioGroup.check(R.id.edit_productivityRecord_productive);
        }
        else{
            radioGroup.check(R.id.edit_productivityRecord_notProductive);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        editActivity.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


}
