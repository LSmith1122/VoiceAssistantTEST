package com.example.lsmith18.mytestapplication.objects;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.example.lsmith18.mytestapplication.data.RequestActionsUtils;

import java.util.ArrayList;
import java.util.List;

public class VoiceRecognition {

    private Intent intent;
    private Context mContext;
    private SpeechRecognizer mSpeechRecognizer;

    public VoiceRecognition(Context context, SpeechRecognizer speechRecognizer) {
        mContext = context;
        mSpeechRecognizer = speechRecognizer;
    }

    public void initSpeechRecognition() {
        startRecognizerIntent();

        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
        mSpeechRecognizer.startListening(intent);
    }

    private void startRecognizerIntent() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
    }

    public class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.i("NORTH", "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.i("NORTH", "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.i("NORTH", "onRmsChanged");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.i("NORTH", "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.i("NORTH", "onEndOfSpeech");
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
            Log.i("NORTH", "onError: " + errorType);
            Toast.makeText(mContext, "Error recognizing voice: " + errorType, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            Log.i("NORTH", "onResults");
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String speechInput = "";
            if (matches.size() != 0) {
                speechInput = matches.get(0);
                if (hasActionableRequest(speechInput)) {
                    //TODO: Execute specific Action based on input
                    String actionType = "None";
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
                            }
                        }
                    }
                    // TODO: Start implicit Intent for Action
                    performActionableRequest(actionType, speechInput, task);
                } else {
                    speechInput = matches.get(0);
                    Toast.makeText(mContext, "No Actionable Request: " + speechInput, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Could not recognize voice", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.i("NORTH", "onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.i("NORTH", "onEvent");
        }

    }

    private void performActionableRequest(String actionType, String input, int task) {
        VoiceAssistant voiceAssistant = new VoiceAssistant(mContext, actionType, input, task);
        voiceAssistant.initModule();
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

//    private void attemptUpdatingFocusedTextBox(String input) {
//        if (mTextBox != null) {
//            input = input.substring(0, 1).toUpperCase() + input.substring(1);
//            if (mTextBox != null) {
//                if (!TextUtils.isEmpty(mTextBox.getText())) {
//                    input = " " + input;
//                }
//                String textBoxText = mTextBox.getText().toString() + input;
//                mTextBox.setText(textBoxText);
//            } else {
//                Log.i("VoiceRecognition", "Cannot locate EditText Views in Activity");
//            }
//        }
//    }

//    public View getFocusedEditText() {
//        ViewGroup rootView = ((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content);
//        ViewGroup parent = (ViewGroup) rootView.getChildAt(0);
//        for (int i = 0; i < parent.getChildCount(); i++) {
//            View view = parent.getChildAt(i);
//            if (view instanceof EditText) {
//                if (view.hasFocus()) {
//                    mHasFocusedEditText = true;
//                    return view;
//                }
//            }
//        }
//
//        Log.i("VoiceRecognition", "RootView Child Count: " + rootView.getChildCount());
//
//        return null;
//    }

    public void stopListening() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
        }
    }

    public void destroy() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }
    }
}
