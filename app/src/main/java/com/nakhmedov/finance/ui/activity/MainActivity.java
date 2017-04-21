package com.nakhmedov.finance.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.nakhmedov.finance.BuildConfig;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.PrefLab;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.adapter.MainMenuAdapter;
import com.nakhmedov.finance.ui.components.UpdateAppDialog;
import com.nakhmedov.finance.ui.entity.Category;

import java.util.List;

import butterknife.BindView;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 3/30/17
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates
 */

public class MainActivity extends BaseActivity {

    private String TAG = MainActivity.class.getCanonicalName();

    @BindView(R.id.gridview) GridView gridView;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Category> categoryList = ((FinanceApp) getApplication()).getDaoSession().getCategoryDao().loadAll();
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

        MainMenuAdapter menuAdapter = new MainMenuAdapter(MainActivity.this);
        gridView.setAdapter(menuAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Clicked position = " + position, Toast.LENGTH_SHORT).show();

                switch (position) {
                    case 0: {
                        Intent categoryIntent = new Intent(MainActivity.this, CategoryActivity.class);
                        categoryIntent.putExtra(CategoryActivity.EXTRA_VIEW_POSITION, CategoryActivity.CATEGORY_POSITION);
                        startActivity(categoryIntent);
                        break;
                    }
                    case 1: {
                        Intent quizIntent = new Intent(MainActivity.this, CategoryActivity.class);
                        quizIntent.putExtra(CategoryActivity.EXTRA_VIEW_POSITION, CategoryActivity.QUIZ_POSITION);
                        startActivity(quizIntent);
                        break;
                    }
                    case 2: {

                        break;
                    }
                    case 3: {

                        break;
                    }
                    case 4: {
                        Intent starredIntent = new Intent(MainActivity.this, CategoryActivity.class);
                        starredIntent.putExtra(CategoryActivity.EXTRA_VIEW_POSITION, CategoryActivity.STARRED_POSITION);
                        startActivity(starredIntent);
                        break;
                    }
                    case 5: {

                        break;
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        boolean isBannerAdsEnabled = mFirebaseRemoteConfig.getBoolean(PrefLab.BANNER_ADS_KEY);
        boolean isInsertialAdsEnabled = mFirebaseRemoteConfig.getBoolean(PrefLab.INSERTIAL_ADS_KEY);
        int appLastVersionCode = (int) mFirebaseRemoteConfig.getLong(PrefLab.APP_LAST_VERSION_KEY);
        prefs.edit()
                .putBoolean(PrefLab.BANNER_ADS_KEY, isBannerAdsEnabled)
                .putBoolean(PrefLab.INSERTIAL_ADS_KEY, isInsertialAdsEnabled)
                .putInt(PrefLab.APP_LAST_VERSION_KEY, appLastVersionCode)
                .apply();

//        loadAds();TODO check ads loaded remote config
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
