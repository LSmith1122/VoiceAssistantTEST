package com.example.lsmith18.mytestapplication.objects;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.lsmith18.mytestapplication.R;

public class VoiceAssistant {

    // TODO: Make this module run an AsyncTask (Retrofit) to make a query and an implicit intent
    // TODO: Use Retrofit for simplicity ***

    private Context mContext;
    private String mActionType;
    private String mUserInput;
    private int mActionIntTask;


    public VoiceAssistant(Context context, String actionType, String userInput, int actionIntTask) {
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
}


/*
*
* https://www.guidearea.com/pocketsphinx-continuous-speech-recognition-android-tutorial/
*
* https://developer.android.com/reference/android/speech/RecognitionService
*
* */