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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.PrefLab;

import static android.app.Activity.RESULT_OK;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/6/17
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final String TAG = SettingsFragment.class.getCanonicalName();

    private static final int REQUEST_INVITE = 101;
    private SharedPreferences prefs;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private ListPreference languageOptionPreference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_preference);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        languageOptionPreference = (ListPreference) findPreference(PrefLab.CHOOSE_SPEECH_LANGUAGE);
        languageOptionPreference.setSummary(prefs.getString(PrefLab.CHOOSE_SPEECH_LANGUAGE, getString(R.string.english_us)));
        Preference sharePreference = findPreference(PrefLab.SHARE);
        Preference ratePreference = findPreference(PrefLab.RATE);
        Preference invitePreference = findPreference(PrefLab.INVITE);

        sharePreference.setOnPreferenceClickListener(this);
        ratePreference.setOnPreferenceClickListener(this);
        invitePreference.setOnPreferenceClickListener(this);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case PrefLab.CHOOSE_SPEECH_LANGUAGE: {
                        String value = sharedPreferences.getString(key, getString(R.string.english_us));
                        languageOptionPreference.setSummary(value);
                        break;
                    }
                    case PrefLab.NTFY_DAILY_TERM: {

                        break;
                    }
                    case PrefLab.NTFY_NEW_VERSION: {

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
            case PrefLab.SHARE: {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_txt));
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                break;
            }
            case PrefLab.RATE: {
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
            case PrefLab.INVITE: {
                sendInvitation();
                break;
            }
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
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

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
}
