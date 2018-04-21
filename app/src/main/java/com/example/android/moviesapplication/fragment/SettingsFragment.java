package com.example.android.moviesapplication.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.example.android.moviesapplication.R;



public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        Preference p = getPreferenceScreen().getPreference(0);
        String value = sharedPreferences.getString(p.getKey(), getString(R.string.pref_sort_default));
        setPreferenceSummary((ListPreference)p, value);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setPreferenceSummary(ListPreference listPreference, String value) {
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                // Set the summary to that label
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        String value = sharedPreferences.getString(preference.getKey(),
                getString(R.string.pref_sort_default));
        setPreferenceSummary((ListPreference)preference, value);
    }
}
