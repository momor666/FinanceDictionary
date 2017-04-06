package com.nakhmedov.finance.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.nakhmedov.finance.R;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/6/17
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private SharedPreferences prefs;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private ListPreference languageOptionPreference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_preference);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        languageOptionPreference = (ListPreference) findPreference("choose_speech_language");
        languageOptionPreference.setSummary(prefs.getString("choose_speech_language", getString(R.string.english_us)));
        Preference sharePreference = findPreference("share");
        Preference ratePreference = findPreference("rate");

        sharePreference.setOnPreferenceClickListener(this);
        ratePreference.setOnPreferenceClickListener(this);




        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case "choose_speech_language": {
                        String value = sharedPreferences.getString(key, getString(R.string.english_us));
                        languageOptionPreference.setSummary(value);
                        break;
                    }
                    case "pref_ntfy_daily_term": {

                        break;
                    }
                    case "pref_ntfy_new_version": {

                        break;
                    }
                }
            }
        };
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String itemKey = preference.getKey();
        switch (itemKey) {
            case "share": {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_txt));
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                break;
            }
            case "rate": {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
                break;
            }
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
