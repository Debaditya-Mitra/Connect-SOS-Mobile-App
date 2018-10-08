package com.chatdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;

public class ChatApplication extends MultiDexApplication{
    private static Context mContextApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferenceUtil.init(getApplicationContext());
        FirebaseApp.initializeApp(getApplicationContext());
        mContextApplication = getApplicationContext();
    }

    /**
     * retrieve application context
     * @return Context
     */
    public static Context getAppContext(){
        return mContextApplication;
    }
}
