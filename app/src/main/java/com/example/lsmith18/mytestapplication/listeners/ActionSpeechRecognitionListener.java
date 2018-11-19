package com.example.lsmith18.mytestapplication.listeners;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.example.lsmith18.mytestapplication.R;
import com.example.lsmith18.mytestapplication.data.RequestActionsUtils;
import com.example.lsmith18.mytestapplication.objects.CommandIntent;

import java.util.ArrayList;
import java.util.List;

public class ActionSpeechRecognitionListener implements RecognitionListener {

    private final String LOG_TAG = "Action Listener";
    private Context mContext;

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        String errorType = "";
        switch (error) {
            case 9:
                errorType = "ERROR_INSUFFICIENT_PERMISSIONS";
                break;
            default:
                errorType = "UNKNOWN";
        }
        Log.i(LOG_TAG, "onError: " + errorType);
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String speechInput = "";
        if (matches.size() != 0) {
            speechInput = matches.get(0);
            if (hasActionableRequest(speechInput)) {
                String actionType = "";
                int task = 0;
                List<List<String>> completeList = RequestActionsUtils.ACTION_COMPLETE_LIST;
                for (int listItemIndex = 0; listItemIndex < completeList.size(); listItemIndex++) {
                    List<String> list = completeList.get(listItemIndex);
                    for (int i = 0; i < list.size(); i++) {
                        String commandString = list.get(i);
                        if (speechInput.contains(commandString)) {
                            if (list == RequestActionsUtils.ACTION_LIST_LOCATE) {
                                actionType = RequestActionsUtils.ACTION_LOCATE;
                                task = listItemIndex;
                            } else {
                                actionType = RequestActionsUtils.ACTION_QUERY;
                                task = listItemIndex;
                            }
                        } else {
                            actionType = RequestActionsUtils.ACTION_QUERY;
                            task = listItemIndex;
                        }
                    }
                }
                // Start implicit Intent for Action
                performActionableRequest(actionType, speechInput, task);
            } else {
                Toast.makeText(mContext, "No Actionable Request:" + speechInput, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.snackbar_recognition_listener_could_not_recognize_voice), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(LOG_TAG, "onEvent");
    }

    private void performActionableRequest(String actionType, String input, int task) {
        CommandIntent commandIntent = new CommandIntent(mContext, actionType, input, task);
        commandIntent.startIntent();
    }

    private boolean hasActionableRequest(String input) {
        for (int listItemIndex = 0; listItemIndex < RequestActionsUtils.ACTION_COMPLETE_LIST.size(); listItemIndex++) {
            List<String> list = RequestActionsUtils.ACTION_COMPLETE_LIST.get(listItemIndex);
            for (int i = 0; i < list.size(); i++) {
                if (input.contains(list.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }
}
