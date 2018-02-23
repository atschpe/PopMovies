package com.example.android.popmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

/**
 * {@link SettingsFragment} displays and retrieves settings information and options.
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref != null) {
            updatePrefSummary(pref,sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.user_selection);//access xml file

        SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference pref = prefScreen.getPreference(i);
            String prefValue = sharedPref.getString(prefScreen.getKey(),"");
            updatePrefSummary(pref, prefValue);
        }
    }

    private void updatePrefSummary(Preference pref, String value) {
        ListPreference listPref = (ListPreference) pref;
        int listPrefIndex = listPref.findIndexOfValue(value);
        if (listPrefIndex >= 0) {
            pref.setSummary(listPref.getEntries()[listPrefIndex]);
        }
    }

    @Override
    public void onStart() {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }
}

