package com.example.lsmith18.mytestapplication.activity;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lsmith18.mytestapplication.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class VoiceAssistantFragment extends PreferenceFragment implements Preference
            .OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            SwitchPreference mVoiceAssistantPref = (SwitchPreference) findPreference(getString(R.string.pref_voice_assistant_key));
            if (mVoiceAssistantPref != null) {
                mVoiceAssistantPref.setOnPreferenceChangeListener(this);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mVoiceAssistantPref.getContext());
                boolean preferenceInt = sharedPreferences.getBoolean(mVoiceAssistantPref.getKey(), false);
                onPreferenceChange(mVoiceAssistantPref, preferenceInt);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof SwitchPreference) {
                SwitchPreference switchPreference = (SwitchPreference) preference;

            }
            return false;
        }
    }
}
