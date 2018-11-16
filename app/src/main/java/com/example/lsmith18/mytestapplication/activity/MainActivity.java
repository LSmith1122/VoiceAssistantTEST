package com.example.lsmith18.mytestapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lsmith18.mytestapplication.R;
import com.example.lsmith18.mytestapplication.data.RequestActionsUtils;
import com.example.lsmith18.mytestapplication.objects.VoiceRecognition;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText mTextBox;
    private ImageButton mSTTButton;
    private Switch mVoiceAssistantSwitch;
    public int mSTTButtonSwitch = 0;
    private String SPACE = " ";
    private String COMMA = ", ";

    private final int PERMISSION_RECORD_AUDIO = 0;

    private final String[] mPermissionList = new String[]{
            Manifest.permission.RECORD_AUDIO};

    VoiceRecognition voiceRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildDescription();
        initAudioPermissionsCheck();
    }

    private void initAudioPermissionsCheck() {
        if (isVoiceAssistantPreferenceEnabled()) {
            if (hasPermissionToRecord()) {
                // Permission is granted
                initVoiceRecognition();
            } else {
                // Permission is not granted
                // TODO: Create an Alert Dialog that permits the User to grant permission to app
                ActivityCompat.requestPermissions(this,
                        mPermissionList,
                        PERMISSION_RECORD_AUDIO);
            }
        }
    }

    private void initVoiceRecognition() {
        setupVoiceRecognition();
        setupVoiceAssistant();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initVoiceRecognition();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupVoiceAssistant() {
        mVoiceAssistantSwitch = findViewById(R.id.voice_assistant_switch);
        mVoiceAssistantSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                } else {

                }
            }
        });
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
                        + SPACE
                        + COMMA;
            }
        }
        String finalDescription = mDescriptionTextView.getText().toString() + descriptionText
                .substring(0, (descriptionText.length() - 2));
        mDescriptionTextView.setText(finalDescription);
    }
    // This is a test. 

    private void setupVoiceRecognition() {
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        voiceRecognition = new VoiceRecognition(this, speechRecognizer);
        mTextBox = findViewById(R.id.textbox_edittext);
        mSTTButton = findViewById(R.id.stt_button);
        mSTTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextBox.setText(mTextBox.getText().toString().trim());
                switch (mSTTButtonSwitch) {
                    case 0:
                        voiceRecognition.initSpeechRecognition();
                        break;
                    case 1:
                        voiceRecognition.stopListening();
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (voiceRecognition != null) {
            voiceRecognition.destroy();
            Toast.makeText(this, "mSpeechRecognizer Destroyed", Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
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
        Toast.makeText(this, "Voice Assistant Enabled?: " + isVoiceAssistantEnabled, Toast.LENGTH_SHORT).show();
        return isVoiceAssistantEnabled;
    }
}
