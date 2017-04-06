package com.nakhmedov.finance.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.adapter.ViewPagerAdapter;
import com.nakhmedov.finance.ui.fragment.CategoryFragment;
import com.nakhmedov.finance.ui.fragment.QuizFragment;
import com.nakhmedov.finance.ui.fragment.StarredFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryActivity extends AppCompatActivity {

    public static final String EXTRA_VIEW_POSITION = "EXTRA_VIEW_POSITION";
    public static final int CATEGORY_POSITION = 0;
    public static final int STARRED_POSITION = 1;
    public static final int QUIZ_POSITION = 2;
    @BindView(R.id.adView) AdView mAdView;
    @BindView(R.id.navigation) BottomNavigationView navigation;
    @BindView(R.id.viewpager) ViewPager viewPager;

    @BindView(R.id.navigation_category) View menu_category;
    @BindView(R.id.navigation_starred) View menu_starred;
    @BindView(R.id.navigation_quiz) View menu_quiz;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_category:
                    viewPager.setCurrentItem(CATEGORY_POSITION);
                    return true;
                case R.id.navigation_starred:
                    viewPager.setCurrentItem(STARRED_POSITION);
                    return true;
                case R.id.navigation_quiz:
                    viewPager.setCurrentItem(QUIZ_POSITION);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        ButterKnife.bind(this);

        int position = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt(EXTRA_VIEW_POSITION);
        }

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("9955D816375FF5AF7DDE1FAA0B2B0413")
                .build();
        mAdView.loadAd(adRequest);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        CategoryFragment categoryListFragment = CategoryFragment.getInstance();
//        StarredFragment starredFragment = StarredFragment.getInstance();
//        QuizFragment quizFragment = QuizFragment.getInstance();
        adapter.addFragment(new CategoryFragment());
        adapter.addFragment(new StarredFragment());
        adapter.addFragment(new QuizFragment());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        selectNavigationMenuItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectNavigationMenuItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void selectNavigationMenuItem(int position) {
        navigation.getMenu().getItem(position).setChecked(true);
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

}
