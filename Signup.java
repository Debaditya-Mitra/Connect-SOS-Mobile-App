package com.chatdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;


public class Signup extends AppCompatActivity implements View.OnClickListener {
    private final String baseUrl = "https://androidchatapp-168d7.firebaseio.com/";
    private EditText username, email, password;
    private Button SignupButton;
    private String user, emai, pass, emailPattern;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
        setListeners();

    }

    //initialising
    private void init() {
        Firebase.setAndroidContext(this);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        SignupButton = findViewById(R.id.registerButton);
        auth = FirebaseAuth.getInstance();
        emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    }

    private void setListeners() {
        SignupButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerButton) {
            emai = email.getText().toString();
            pass = password.getText().toString();
            user = username.getText().toString();
            signup();
        }
    }

    private void signup() {

        if (user.equals("")) {
            email.setError("can't be blank");
        } else if (pass.equals("")) {
            password.setError("can't be blank");
        } else if (emai.length() > 0 && !emai.matches(emailPattern)) {
            email.setError("please enter valid email address");
        } else if (user.length() < 5) {
            username.setError("at least 5 characters long");
        } else if (pass.length() < 6) {
            password.setError("at least 6 characters long");
        } else {
            final ProgressDialog pd = new ProgressDialog(Signup.this);
            pd.setMessage("Loading...");
            pd.show();

            auth.createUserWithEmailAndPassword(emai, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                // Log.d(TAG, "createUserWithEmail:success");
                                final FirebaseUser userDetails = auth.getCurrentUser();
                                SharedPreferenceUtil.putValue("UserId",userDetails.getUid());
                                String url = baseUrl + "Users.json";

                                StringRequest request = new StringRequest(Request.Method.GET, url, new
                                        com.android.volley.Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String s) {
                                                Firebase reference = new Firebase(baseUrl + "Users");

                                                if (s.equals("null")) {
                                                    String key = userDetails.getUid();
                                                    reference.child(key).child("password").setValue(pass);
                                                    reference.child(key).child("username").setValue(user);
                                                    reference.child(key).child("email").setValue(emai);
                                                    //reference.child(user).child("password").setValue(pass);
                                                    Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_LONG).show();
                                                } else {
                                                    try {
                                                        JSONObject obj = new JSONObject(s);
                                                        //if (!obj.has(user)) {
                                                        String key = userDetails.getUid();
                                                        reference.child(key).child("password").setValue(pass);
                                                        reference.child(key).child("username").setValue(user);
                                                        reference.child(key).child("email").setValue(emai);
                                                        //reference.child(user).child("password").setValue(pass);
                                                        Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_LONG).show();
                                                       /* } else {
                                                            Toast.makeText(getApplicationContext(), "username already exists", Toast.LENGTH_LONG).show();
                                                        }*/

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                pd.dismiss();
                                            }

                                        }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        System.out.println("" + volleyError);
                                        pd.dismiss();
                                    }
                                });

                                RequestQueue rQueue = Volley.newRequestQueue(Signup.this);
                                rQueue.add(request);
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                 Log.w("Login", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Signup.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });

        }
    }
}