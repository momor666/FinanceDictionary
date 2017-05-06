package com.nakhmedov.finance.ui.activity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.nakhmedov.finance.BuildConfig;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.constants.PrefLab;
import com.nakhmedov.finance.ui.components.UpdateAppDialog;
import com.nakhmedov.finance.ui.fragment.MainFragment;
import com.nakhmedov.finance.ui.receiver.DailyReceiver;
import com.nakhmedov.finance.util.AndroidUtil;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 3/30/17
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates
 */

public class MainActivity extends BaseActivity {

    private String TAG = MainActivity.class.getCanonicalName();

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static final int DAILY_REQ_CODE = 101;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            MainFragment mainFragment = MainFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, mainFragment)
                    .commit();
        }

        prefs.edit().putBoolean(PrefLab.DISPLAY_INSERTIAL, true).apply();

        /*Firebase*/
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings mRemoteConfigSettings = new FirebaseRemoteConfigSettings
                .Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        mFirebaseRemoteConfig.setConfigSettings(mRemoteConfigSettings);
//        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        /*Fetch remote config*/

        fetchRemoteConfig();
        boolean isDailyNotification = prefs.getBoolean("pref_ntfy_daily_term", true);
        if (isDailyNotification) {
            Intent intent = new Intent(MainActivity.this, DailyReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DAILY_REQ_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, AndroidUtil.getAlarmTime(),  AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(ContextConstants.NTFY_DAILY_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
            case R.id.action_search: {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            }
            case R.id.action_settings: {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void fetchRemoteConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that
        // each fetch goes to the server. This should not be used in release
        // builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings()
                .isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.w(TAG, "Success onComplete fetching config");
                            mFirebaseRemoteConfig.activateFetched();
                            applyConfigs();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error fetching config: " +
                                e.getMessage());
                        applyConfigs();
                    }
                });
    }

    private void applyConfigs() {
        Log.i(TAG, mFirebaseRemoteConfig.getBoolean(PrefLab.BANNER_ADS_KEY) +"");
        Log.i(TAG, mFirebaseRemoteConfig.getBoolean(PrefLab.INSERTIAL_ADS_KEY) + "");
        Log.i(TAG, mFirebaseRemoteConfig.getLong(PrefLab.APP_LAST_VERSION_KEY) +"");
        Log.i(TAG, mFirebaseRemoteConfig.getLong(PrefLab.UPDATE_DATA_PERIOD) +"");
        boolean isBannerAdsEnabled = mFirebaseRemoteConfig.getBoolean(PrefLab.BANNER_ADS_KEY);
        boolean isInsertialAdsEnabled = mFirebaseRemoteConfig.getBoolean(PrefLab.INSERTIAL_ADS_KEY);
        int appLastVersionCode = (int) mFirebaseRemoteConfig.getLong(PrefLab.APP_LAST_VERSION_KEY);
        int updatePeriodInDay = (int) mFirebaseRemoteConfig.getLong(PrefLab.UPDATE_DATA_PERIOD);
        prefs.edit()
                .putBoolean(PrefLab.BANNER_ADS_KEY, isBannerAdsEnabled)
                .putBoolean(PrefLab.INSERTIAL_ADS_KEY, isInsertialAdsEnabled)
                .putInt(PrefLab.APP_LAST_VERSION_KEY, appLastVersionCode)
                .putInt(PrefLab.UPDATE_DATA_PERIOD, updatePeriodInDay)
                .apply();

        loadAds();
        checkAppNewVersion(appLastVersionCode);

    }


    private void checkAppNewVersion(int appLastVersionCode) {
        int currentVersionCode = getCurrentVersionCode();
        if (appLastVersionCode > currentVersionCode) {
            if (prefs.getBoolean(PrefLab.NTFY_NEW_VERSION, true)) {
                UpdateAppDialog updateAppDialog = new UpdateAppDialog();
                updateAppDialog.showUpdateDialog(MainActivity.this);
            }
        }
    }

    private int getCurrentVersionCode() {
        try {
            PackageInfo managerInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return managerInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
