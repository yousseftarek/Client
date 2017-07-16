package com.example.mylife_mk3.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.DatePicker;

import com.example.mylife_mk3.R;


/**
 * Created by Tarek on 20-Feb-2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new DatePickerDialog(getActivity(), this, R.integer.birth_year, R.integer.birth_month, R.integer.birth_day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.shared_preferences_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("birth_day", dayOfMonth);
        editor.putInt("birth_month", month);
        editor.putInt("birth_year", year);
        editor.commit();
    }
}
