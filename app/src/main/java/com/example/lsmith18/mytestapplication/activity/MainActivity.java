package com.example.lsmith18.mytestapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.speech.RecognitionService;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lsmith18.mytestapplication.R;
import com.example.lsmith18.mytestapplication.data.RequestActionsUtils;
import com.example.lsmith18.mytestapplication.objects.VoiceAssistant;
import com.example.lsmith18.mytestapplication.objects.VoiceRecognition;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private Intent mBindingServiceIntent;
    private ImageButton mVoiceRecordingButton;
    private final String SPACE = " ";
    private final String COMMA = ",";
    private int mBindFlag;
    private boolean mRecognitionServiceInstalled;

    private Messenger mServiceMessenger;
    private edu.cmu.pocketsphinx.SpeechRecognizer mSpeechRecognizer;
    private VoiceRecognition voiceRecognition;
    private VoiceAssistant mVoiceAssistant;
    private final int PERMISSION_RECORD_AUDIO = 0;
    private final String[] mPermissionList = new String[]{
            Manifest.permission.RECORD_AUDIO};


//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mServiceMessenger = new Messenger(service);
//            Message msg = new Message();
//            msg.what = VoiceCommandService.MSG_RECOGNIZER_START_LISTENING;
//
//            try {
//                mServiceMessenger.send(msg);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mServiceMessenger = null;
//        }
//
//    }; // mServiceConnection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        buildDescription();
        setupVoiceRecordingButton();
        determineAccessToVoiceAssistant();
    }

    private void determineAccessToVoiceAssistant() {
        if (isVoiceAssistantPreferenceEnabled()) {
            if (!hasPermissionToRecord()) {
                ActivityCompat.requestPermissions(this,
                        mPermissionList,
                        PERMISSION_RECORD_AUDIO);
                if (mVoiceAssistant != null) {
                    mVoiceAssistant.onStop();
                }
            } else {
                setupVoiceAssistant();
                mVoiceAssistant.startContinuousListening();
            }
        }
    }

    private void checkForRecognitionServiceInstallation() {
        if (isRecognitionServiceInstalled(getPackageManager(), getComponentName())) {
            mRecognitionServiceInstalled = true;
            Snackbar.make(findViewById(android.R.id.content),
                    getResources().getString(R.string.snackbar_recognition_installed),
                    Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            mRecognitionServiceInstalled = false;
            Snackbar.make(findViewById(android.R.id.content),
                    getResources().getString(R.string.snackbar_recognition_not_installed),
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private boolean isRecognitionServiceInstalled(PackageManager packageManager, ComponentName componentName) {
        List<ResolveInfo> services = packageManager.queryIntentServices(new Intent(RecognitionService.SERVICE_INTERFACE), 0);
        for (ResolveInfo resolveInfo : services) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo == null) {
                Log.i("Recognition Service", "serviceInfo == null");
                continue;
            }
            if (componentName.equals(new ComponentName(serviceInfo.packageName, serviceInfo.name))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupVoiceAssistant();
                    mVoiceAssistant.startContinuousListening();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupVoiceAssistant() {
//        Intent service = new Intent(this, VoiceCommandService.class);
//        Log.d("MainActivity", "Start Service");
//        startService(service);
//        mBindFlag = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH ? 0 : Context.BIND_ABOVE_CLIENT;

        Toast.makeText(mContext, "Setting up Voice Assistant", Toast.LENGTH_SHORT).show();
        if (mVoiceAssistant == null) {
            try {
                Assets assets = new Assets(this);
                File assetsDir = assets.syncAssets();
                mSpeechRecognizer = SpeechRecognizerSetup.defaultSetup()
                        .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                        .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                        .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                        .getRecognizer();
                mVoiceAssistant = new VoiceAssistant(this, mSpeechRecognizer, assetsDir);
                Log.d("NORTH", "Success setting up Voice Assistant");
            } catch (IOException e) {
                Log.d("NORTH", "Error setting up Voice Assistant: ", e);
            }
        }
    }

    private boolean hasPermissionToRecord() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void buildDescription() {
        TextView mDescriptionTextView = findViewById(R.id.description_textview);
        String descriptionText = "";
        for (int listNumber = 0; listNumber < RequestActionsUtils.ACTION_COMPLETE_LIST.size(); listNumber++) {
            List<String> currentList = RequestActionsUtils.ACTION_COMPLETE_LIST.get(listNumber);
            for (int i = 0; i < currentList.size(); i++) {
                String actionItem = currentList.get(i);
                descriptionText = descriptionText
                        + actionItem
                        + COMMA
                        + SPACE;
            }
        }
        String finalDescription = mDescriptionTextView.getText().toString() + descriptionText
                .substring(0, (descriptionText.length() - 2));
        mDescriptionTextView.setText(finalDescription);
    }

    private void setupVoiceRecordingButton() {
        mVoiceRecordingButton = findViewById(R.id.voice_recording_button);
        mVoiceRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Button Clicked", Toast.LENGTH_SHORT).show();
                if (!hasPermissionToRecord()) {
                    Toast.makeText(mContext, "Access Denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(((Activity) mContext),
                            mPermissionList,
                            PERMISSION_RECORD_AUDIO);
                } else {
                    if (voiceRecognition == null) {
                        createVoiceRecognition();
                    }
                    voiceRecognition.initSpeechRecognition();
                }
            }
        });
    }

    private void createVoiceRecognition() {
        if (voiceRecognition == null) {
            voiceRecognition = new VoiceRecognition(this, SpeechRecognizer.createSpeechRecognizer(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isVoiceAssistantPreferenceEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isVoiceAssistantEnabled = sharedPreferences.getBoolean(getString(R.string.pref_voice_assistant_key), false);
        return isVoiceAssistantEnabled;
    }

    @Override
    protected void onStart() {
//        mBindingServiceIntent = new Intent(this, VoiceCommandService.class);
//        bindService(mBindingServiceIntent, mServiceConnection, mBindFlag);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mVoiceAssistant != null) {
            mVoiceAssistant.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        if (mServiceMessenger != null) {
//            unbindService(mServiceConnection);
//            mServiceMessenger = null;
//        }
//        if (mBindingServiceIntent != null) {
//            stopService(mBindingServiceIntent);
//        }
//        if (voiceRecognition != null) {
//            voiceRecognition.destroy();
//            Toast.makeText(this, "mSpeechRecognizer Destroyed", Toast.LENGTH_SHORT).show();
//        }
        super.onDestroy();
    }
}
