package com.example.lsmith18.mytestapplication.objects;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.example.lsmith18.mytestapplication.data.RequestActionsUtils;
import com.example.lsmith18.mytestapplication.listeners.ActionSpeechRecognitionListener;

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

        ActionSpeechRecognitionListener listener = new ActionSpeechRecognitionListener();
        listener.setContext(mContext);
        mSpeechRecognizer.setRecognitionListener(listener);
        mSpeechRecognizer.startListening(intent);
    }

    private void startRecognizerIntent() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
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
