package com.example.lsmith18.mytestapplication.objects;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.lsmith18.mytestapplication.data.RequestActionsUtils;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class VoiceAssistant implements RecognitionListener {

    // TODO: Make this object run an AsyncTask (Retrofit) API call
    // TODO: Use Retrofit for simplicity ***

    private Context mContext;
    private SpeechRecognizer mRecognizer;
    private File mFile;
//    private VoiceCommandService voiceCommandService;


    public VoiceAssistant(Context context, SpeechRecognizer recognizer, File file) {
        mContext = context;
        mRecognizer = recognizer;
        mFile = file;
//        voiceCommandService = new VoiceCommandService();
    }

    public void startContinuousListening() {
//        Intent service = new Intent(mContext, VoiceCommandService.class);
//        mContext.startService(service);
//
//        Message msg = new Message();
//        msg.what = VoiceCommandService.MSG_RECOGNIZER_START_LISTENING;
//
//        try {
//            voiceCommandService.mServerMessenger.send(msg);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        if (mContext != null) {
            runRecognizerSetup(mContext, mRecognizer, mFile, this);
        } else {
            Toast.makeText(mContext, "Context is Null", Toast.LENGTH_SHORT).show();
        }
    }

    private static void runRecognizerSetup(final Context context,
                                           final SpeechRecognizer recognizer,
                                           final File assetDir,
                                           final RecognitionListener listener) {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    setupRecognizer(listener, assetDir, recognizer);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    System.out.println(result.getMessage());
                } else {
                    switchSearch(RequestActionsUtils.KWS_SEARCH, recognizer);
                }
            }
        }.execute();
    }

    private static void setupRecognizer(RecognitionListener listener, File assetsDir, SpeechRecognizer recognizer) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                // Disable this line if you don't want recognizer to save raw
                // audio files to app's storage
                //.setRawLogDir(assetsDir)
                .getRecognizer();
        recognizer.addListener(listener);
        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(RequestActionsUtils.KWS_SEARCH, RequestActionsUtils.KEYPHRASE);
        // Create your custom grammar-based search
        File menuGrammar = new File(assetsDir, "mymenu.gram");
        recognizer.addGrammarSearch(RequestActionsUtils.MENU_SEARCH, menuGrammar);
    }

    public void cancelContinuousListening() {
//        Message msg = new Message();
//        msg.what = VoiceCommandService.MSG_RECOGNIZER_CANCEL;
//
//        try {
//            voiceCommandService.mServerMessenger.send(msg);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    private static void switchSearch(String searchName, SpeechRecognizer mRecognizer) {
        mRecognizer.stop();
        if (searchName.equals(RequestActionsUtils.KWS_SEARCH)) {
            mRecognizer.startListening(searchName);
        } else {
            mRecognizer.startListening(searchName, 10000);
        }
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        if (!mRecognizer.getSearchName().equals(RequestActionsUtils.KWS_SEARCH)) {
            switchSearch(RequestActionsUtils.KWS_SEARCH, mRecognizer);
        }
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }
        String text = hypothesis.getHypstr();
        switch (text) {
            case RequestActionsUtils.KEYPHRASE:
                switchSearch(RequestActionsUtils.MENU_SEARCH, mRecognizer);
                break;
            case "hello":
                Toast.makeText(mContext, "Hello to you too!", Toast.LENGTH_SHORT).show();
                break;
            case "good morning":
                Toast.makeText(mContext, "Good morning to you too!", Toast.LENGTH_SHORT).show();
                break;
            default:
                System.out.println(hypothesis.getHypstr());
                break;
        }
    }


    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            System.out.println(hypothesis.getHypstr());
        }
    }

    @Override
    public void onError(Exception e) {
        System.out.println(e.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(RequestActionsUtils.KWS_SEARCH, mRecognizer);
    }

    public void onStop() {
        if (mRecognizer != null) {
            mRecognizer.cancel();
            mRecognizer.shutdown();
        }
    }
}
