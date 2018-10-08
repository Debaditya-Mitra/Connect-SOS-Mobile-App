package com.chatdemo.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chatdemo.FusedLocationSingleton;
import com.chatdemo.R;
import com.chatdemo.SharedPreferenceUtil;
import com.chatdemo.Signup;
import com.chatdemo.UserDetails;
import com.chatdemo.UsersListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String baseUrl = "https://androidchatapp-168d7.firebaseio.com/";
    EditText email, password;
    Button loginButton, SignupButton;
    private FirebaseAuth auth;
    String emai, pass;
    private View rootView;
    private TextView mPositionContainer;
    public final String LBM_EVENT_LOCATION_UPDATE = "lbmLocationUpdate";
    public final String INTENT_FILTER_LOCATION_UPDATE = "intentFilterLocationUpdate";


    public void checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        if (res != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }




 /*   public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

            }

        }
    }
*/


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        setListeners();

    }

    @Override
    public void onResume() {
        super.onResume();
        if( this.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // start location updates
            FusedLocationSingleton.getInstance().startLocationUpdates();
            // register observer for location updates
            LocalBroadcastManager.getInstance(LoginActivity.this).registerReceiver(mLocationUpdated,
                    new IntentFilter(INTENT_FILTER_LOCATION_UPDATE));
        }else {
            checkLocationPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop location updates
        FusedLocationSingleton.getInstance().stopLocationUpdates();
        // unregister observer
        LocalBroadcastManager.getInstance(LoginActivity.this).unregisterReceiver(mLocationUpdated);
    }


    private void init() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        SignupButton = findViewById(R.id.registerButton);
        auth = FirebaseAuth.getInstance();
    }

    private void setListeners() {
        SignupButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerButton:
                Intent registerIntent = new Intent(this, Signup.class);
                startActivity(registerIntent);
                break;
            case R.id.loginButton:
                emai = email.getText().toString();
                pass = password.getText().toString();
                login();
                break;

        }


    }

    private void login() {


        emai = email.getText().toString();
        pass = password.getText().toString();

        if (emai.equals("")) {
            email.setError("can't be blank");
        } else if (pass.equals("")) {
            password.setError("can't be blank");
        } else {
            String url = baseUrl + "Users.json";
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Loading...");
            pd.show();

            auth.signInWithEmailAndPassword(emai, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Login", "signInWithEmail:success");
                                //UserDetails.username = emai;
                                //UserDetails.password = pass;
                                FirebaseUser userDetails = auth.getCurrentUser();
                                SharedPreferenceUtil.putValue("UserId", userDetails.getUid());
                                UserDetails.userId = userDetails.getUid();
                                startActivity(new Intent(getApplicationContext(), UsersListActivity.class));


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Login", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });

            /*StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    if(s.equals("null")){
                        Toast.makeText(getApplicationContext(), "user not found", Toast.LENGTH_LONG).show();
                    }
                    else{
                        try {
                            JSONObject obj = new JSONObject(s);

                            if(!obj.has(emai)){
                                Toast.makeText(getApplicationContext(), "user not found", Toast.LENGTH_LONG).show();
                            }
                            else if(obj.getJSONObject(emai).getString("password").equals(pass)){
                                UserDetails.username = emai;
                                UserDetails.password = pass;
                                startActivity(new Intent(getApplicationContext(), UsersListActivity.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "incorrect password", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    pd.dismiss();
                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError);
                    pd.dismiss();
                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(this);
            rQueue.add(request);*/
        }

    }


    private BroadcastReceiver mLocationUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Location location = (Location) intent.getParcelableExtra(LBM_EVENT_LOCATION_UPDATE);
               // mPositionContainer.setText("Lat: " + location.getLatitude()+ " Lon: " + location.getLongitude());
                Log.e("Login","Lat: " + location.getLatitude()+ " Lon: " + location.getLongitude());
            } catch (Exception e) {
                //
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FusedLocationSingleton.getInstance().startLocationUpdates();
                    // register observer for location updates
                    LocalBroadcastManager.getInstance(LoginActivity.this).registerReceiver(mLocationUpdated,
                            new IntentFilter(INTENT_FILTER_LOCATION_UPDATE));
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

            }

        }
    }


}



