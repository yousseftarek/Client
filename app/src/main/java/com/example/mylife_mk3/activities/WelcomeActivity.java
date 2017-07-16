package com.example.mylife_mk3.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mylife_mk3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private String signInURL = "http://10.0.2.2:8080/api/authenticate";
    private ProgressBar progressBar;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_signin);
        queue = Volley.newRequestQueue(WelcomeActivity.this);

        Button signup = (Button) findViewById(R.id.signUpButton);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        Button button = (Button) findViewById(R.id.signInButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(ProgressBar.VISIBLE);

                TextView emailText = (TextView) findViewById(R.id.signInEmail);
                String email = emailText.getText().toString();

                TextView passText = (TextView) findViewById(R.id.signinPassword);
                String password = passText.getText().toString();

//                LoginAsyncTask login = new LoginAsyncTask(WelcomeActivity.this);
//
//                login.execute(email, password);

                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                JSONObject jsonObject = new JSONObject(params);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, signInURL, jsonObject, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean success = response.getBoolean("success");
                                    if(success){
                                        JSONObject user = response.getJSONObject("user");
                                        JSONObject home = user.getJSONObject("home");
                                        JSONObject work = user.getJSONObject("work");
//                                        JSONArray tokens = user.getJSONArray("tokens");

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

                                        progressBar.setVisibility(ProgressBar.INVISIBLE);

                                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                        startActivity(intent);

                                    }
                                    else {
                                        Snackbar.make(WelcomeActivity.this.findViewById(R.id.welcomeCoordinator), response.getString("message"), Snackbar.LENGTH_LONG).show();
                                    }
                                    //Toast.makeText(WelcomeActivity.this, success, Toast.LENGTH_LONG).show();
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
        });
    }

    private class LoginAsyncTask extends AsyncTask<String, Void, Void> {

        private boolean success = false;
        Activity context;

        public LoginAsyncTask(Activity activity){
            this.context = activity;
        }

        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }
        @Override
        protected Void doInBackground(String... params) {

            Map<String, String> requestParams = new HashMap<String, String>();
            requestParams.put("email", params[0]);
            requestParams.put("password", params[1]);

            JSONObject jsonObject = new JSONObject(requestParams);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, signInURL, jsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                if(success){
                                    JSONObject user = response.getJSONObject("user");
                                    JSONObject home = user.getJSONObject("home");
                                    JSONObject work = user.getJSONObject("work");
//                                        JSONArray tokens = user.getJSONArray("tokens");

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

                                    LoginAsyncTask.this.success = true;
                                    Log.d("NOT", LoginAsyncTask.this.success+"");
                                }
                                else {
                                    Snackbar.make(WelcomeActivity.this.findViewById(R.id.welcomeCoordinator), response.getString("message"), Snackbar.LENGTH_LONG).show();
                                }
                                //Toast.makeText(WelcomeActivity.this, success, Toast.LENGTH_LONG).show();
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

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            progressBar.setVisibility(ProgressBar.INVISIBLE);

            if(LoginAsyncTask.this.success){

                LoginAsyncTask.this.success = false;
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                context.startActivity(intent);
            }
            else{
                Log.d("HERE: ", "FALSE");
            }

        }
    }
}


//                                        intent.putExtra("username", user.getString("name"));
//                                        intent.putExtra("email", user.getString("email"));
//                                        intent.putExtra("birthday", user.getString("birthday"));
//                                        intent.putExtra("gender", user.getString("gender"));
//                                        intent.putExtra("databaseID", user.getString("_id"));
//                                        intent.putExtra("homeLat", home.getString("lat"));
//                                        intent.putExtra("homeLong", home.getString("long"));
//                                        intent.putExtra("workLat", work.getString("lat"));
//                                        intent.putExtra("workLong", work.getString("long"));
//
//                                        intent.putExtra("token", response.getString("token"));