package com.example.lsmith18.mytestapplication.module;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.lsmith18.mytestapplication.R;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class VoiceAssistantModule {

    // TODO: Make this module run an AsyncTask (Retrofit) to make a query and an implicit intent
    // TODO: Use Retrofit for simplicity ***

    private Context mContext;
    private String mActionType;
    private String mUserInput;
    private int mActionIntTask;


    public VoiceAssistantModule(Context context, String actionType, String userInput, int actionIntTask) {
        mContext = context;
        mActionType = actionType;
        mUserInput = userInput;
        mActionIntTask = actionIntTask;
    }

    public void initModule() {
        Uri navigationUri = Uri.parse(mActionType + mUserInput);
        Intent intent = new Intent(Intent.ACTION_VIEW, navigationUri);
        mContext.startActivity(intent);
    }

    public void initVoiceAssistant() {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getResources().getString(R.string.pref_voice_assistant_name), 0);
//        int prefValue = mContext.getResources().getInteger()
//        preferences.getInt()
    }
}
