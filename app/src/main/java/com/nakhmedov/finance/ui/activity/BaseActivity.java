package com.nakhmedov.finance.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.PrefLab;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/8/17
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates
 */

public class BaseActivity extends AppCompatActivity {

    @Nullable @BindView(R.id.adView) AdView mAdView;
    @Nullable @BindView(R.id.toolbar) Toolbar mToolbar;

    private InterstitialAd mInterstitialAd;

    public SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResourceId());

        ButterKnife.bind(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(needToolbar()) initToolbar();

        loadAds();
        loadInsertialAds();
    }

    public boolean needToolbar() {
        return true;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    public int getLayoutResourceId() {
        return 0;
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
    }
    public void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void showBackBtn() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0.2f);
    }

    public void hideBackBtn() {
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    protected void loadAds() {
        boolean isBannerAdsEnabled = prefs.getBoolean(PrefLab.BANNER_ADS_KEY, false);
        if (mAdView != null) {
            if (isBannerAdsEnabled) {
                mAdView.setVisibility(View.VISIBLE);
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice("9955D816375FF5AF7DDE1FAA0B2B0413")
                        .build();
                mAdView.loadAd(adRequest);
            }
            else {
                mAdView.setVisibility(View.GONE);
            }
        }
    }

    private void loadInsertialAds() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.insertial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                displayInsertialAds();
            }
        });

    }

    public void requestNewInterstitial() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        boolean isInsertialAdsEnabled = prefs.getBoolean(PrefLab.INSERTIAL_ADS_KEY, false);
        if (isInsertialAdsEnabled && !mInterstitialAd.isLoading()
                && !mInterstitialAd.isLoaded() && mInterstitialAd.getAdUnitId() != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("9955D816375FF5AF7DDE1FAA0B2B0413")
                    .build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    private void displayInsertialAds() {
        boolean isFirstTime = prefs.getBoolean(PrefLab.DISPLAY_INSERTIAL, true);

        if (mInterstitialAd.isLoaded() && isFirstTime) {
            mInterstitialAd.show();
            prefs.edit().putBoolean(PrefLab.DISPLAY_INSERTIAL, false).apply();
        }
    }

    public void shareViaApp(String categoryName) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, categoryName + "\n" +
                getString(R.string.app_url));
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }
}
