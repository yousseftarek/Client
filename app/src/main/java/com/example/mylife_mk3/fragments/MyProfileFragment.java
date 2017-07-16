package com.example.mylife_mk3.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mylife_mk3.R;
import com.example.mylife_mk3.activities.MainActivity;
import com.example.mylife_mk3.activities.ProfileSettings;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tarek on 18-Feb-2017.
 */

public class MyProfileFragment extends android.app.Fragment {

//    private Map<String, String> user;
//    private String serverToken;

    public MyProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
//        Bundle arguments = getArguments();


//        setUser((HashMap)arguments.getSerializable("hashmap"));
//        setServerToken(arguments.getString("token"));
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key), context.MODE_PRIVATE);


        LinearLayout location = (LinearLayout) rootView.findViewById(R.id.location_layout);
        TextView name = (TextView) rootView.findViewById(R.id.username);
        name.setText(sharedPreferences.getString("username", "John Doe"));

        TextView email = (TextView) rootView.findViewById(R.id.user_email_text);
        email.setText(sharedPreferences.getString("email", "DUMMY"));

        TextView birthday = (TextView) rootView.findViewById(R.id.user_birthday_text);
        birthday.setText(sharedPreferences.getString("birthday", "DUMMY"));

        TextView gender = (TextView) rootView.findViewById(R.id.user_gender_text);
        gender.setText(sharedPreferences.getString("gender", "DUMMY"));

        Button settings = (Button) rootView.findViewById(R.id.profile_settings_button);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileSettings.class);
                getActivity().startActivity(intent);
            }
        });

        if(sharedPreferences.getString("gender", "DUMMY").equals("Female")){
            ImageView genderImage = (ImageView) rootView.findViewById(R.id.user_gender_image);
            genderImage.setImageResource(R.drawable.gender_female);

            CircleImageView profileImage = (CircleImageView) rootView.findViewById(R.id.profile_image);
            profileImage.setImageResource(R.drawable.lolo2);
        }

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Favourite Location: " + getString(R.string.fav_location), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        return rootView;
    }


}
