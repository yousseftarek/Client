package com.example.mylife_mk3.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mylife_mk3.R;
import com.example.mylife_mk3.fragments.AddMealRecordFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener{

    private String signUpURL = "http://10.0.2.2:8080/api/signup";
    private ProgressBar progressBar;
    RequestQueue queue;
    private EditText name, email, password;
    private TextInputLayout nameLayout, emailLayout, passwordLayout;
    private String gender;
    private Button birthday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        progressBar = (ProgressBar) findViewById(R.id.signup_progress);
        queue = Volley.newRequestQueue(SignUpActivity.this);

        Spinner spinner = (Spinner) findViewById(R.id.signup_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        name = (EditText) findViewById(R.id.signup_name);
        email = (EditText) findViewById(R.id.signup_email);
        password = (EditText) findViewById(R.id.signup_password);
        birthday = (Button) findViewById(R.id.signup_birthday);

        nameLayout = (TextInputLayout) findViewById(R.id.signup_name_layout);
        emailLayout = (TextInputLayout) findViewById(R.id.signup_email_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.signup_password_layout);

        name.addTextChangedListener(new MyTextwatcher(name));
        email.addTextChangedListener(new MyTextwatcher(email));
        password.addTextChangedListener(new MyTextwatcher(password));

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        SignUpActivity.this,
                        1990,
                        0,
                        1);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        final Button submit = (Button) findViewById(R.id.signup_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm(){
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        addUserInDB();
    }

    private void addUserInDB(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name.getText().toString());
        params.put("email", email.getText().toString());
        params.put("password", password.getText().toString());
        params.put("gender", gender);
        params.put("birthday", birthday.getText().toString());

        JSONObject object = new JSONObject(params);

        progressBar.setVisibility(ProgressBar.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, signUpURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean requestSuccess = response.getBoolean("success");
                    if (requestSuccess){
                        progressBar.setVisibility(ProgressBar.GONE);

                        JSONObject user = response.getJSONObject("user");
                        JSONObject home = user.getJSONObject("home");
                        JSONObject work = user.getJSONObject("work");

                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        editor.putString("email", user.getString("email"));
                        editor.putString("username", user.getString("name"));
                        editor.putString("birthday", user.getString("birthday"));
                        editor.putString("gender", user.getString("gender"));
                        editor.putString("databaseID", user.getString("_id"));

                        if(! (home.isNull("lat") && home.isNull("long"))){
                            editor.putString("homeLat", home.getString("lat"));
                            editor.putString("homeLong", home.getString("long"));
                        }
                        if(! (work.isNull("lat") && work.isNull("long"))){
                            editor.putString("workLong", work.getString("long"));
                            editor.putString("workLong", work.getString("long"));
                        }

                        editor.putString("token", response.getString("token"));

                        editor.commit();

                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
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

    private boolean validateName() {
        if (name.getText().toString().trim().isEmpty()) {
            nameLayout.setError("Please Enter Your Full Name!");
            requestFocus(name);
            return false;
        } else {
            nameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String emailString = email.getText().toString().trim();

        if (emailString.isEmpty() || !isValidEmail(emailString)) {
            emailLayout.setError("Please Enter A Valid Email!");
            requestFocus(email);
            return false;
        } else {
            emailLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()) {
            passwordLayout.setError("Please Enter A Valid Password!");
            requestFocus(password);
            return false;
        } else {
            passwordLayout.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gender = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        birthday.setText(dayOfMonth + "-" + (monthOfYear+ 1) + "-" + year);
    }

    private class MyTextwatcher implements TextWatcher {

        private View view;

        private MyTextwatcher(View view){
            this.view=view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.signup_name:
                    validateName();
                    break;
                case R.id.signup_email:
                    validateEmail();
                    break;
                case R.id.signup_password:
                    validatePassword();
                    break;
            }
        }
    }

}
