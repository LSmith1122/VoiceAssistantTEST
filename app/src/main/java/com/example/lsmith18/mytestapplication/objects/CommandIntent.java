package com.example.lsmith18.mytestapplication.objects;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CommandIntent {

    private Context mContext;
    private String mActionType;
    private String mUserInput;
    private int mActionIntTask;

    public CommandIntent(Context context, String actionType, String userInput, int actionIntTask) {
        mContext = context;
        mActionType = actionType;
        mUserInput = userInput;
        mActionIntTask = actionIntTask;
    }

    public void startIntent() {
        Uri navigationUri = Uri.parse(mActionType + mUserInput);
        Intent intent = new Intent(Intent.ACTION_VIEW, navigationUri);
        mContext.startActivity(intent);
    }
}
