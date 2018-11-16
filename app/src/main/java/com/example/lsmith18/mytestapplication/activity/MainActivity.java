package com.example.lsmith18.mytestapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.RecognitionService;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private String SPACE = " ";
    private String COMMA = ", ";

    VoiceRecognition voiceRecognition;
    SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildDescription();
        setupVoiceRecognition();
        if (isVoiceAssistantEnabled()) {
            // TODO: Do something...
//        setupVoiceAssistant();
        }
    }

    private boolean isVoiceAssistantEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(getString(R.string.pref_voice_assistant_key), false);
    }

    private void setupVoiceAssistant() {
        // TODO: Do something...
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

    private void setupVoiceRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        voiceRecognition = new VoiceRecognition(this, speechRecognizer);
        mTextBox = findViewById(R.id.textbox_edittext);
        mSTTButton = findViewById(R.id.stt_button);
        mSTTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceRecognition.initSpeechRecognition();
            }
        });
    }

    @Override
    protected void onDestroy() {
        voiceRecognition.destroy();
        Toast.makeText(this, "mSpeechRecognizer Destroyed", Toast.LENGTH_SHORT).show();
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
}
