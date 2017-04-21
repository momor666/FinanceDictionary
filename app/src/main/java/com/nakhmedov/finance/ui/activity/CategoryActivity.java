package com.nakhmedov.finance.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.adapter.ViewPagerAdapter;
import com.nakhmedov.finance.ui.fragment.CategoryFragment;
import com.nakhmedov.finance.ui.fragment.QuizFragment;
import com.nakhmedov.finance.ui.fragment.StarredFragment;

import butterknife.BindView;

public class CategoryActivity extends BaseActivity {

    public static final String EXTRA_VIEW_POSITION = "EXTRA_VIEW_POSITION";
    public static final int CATEGORY_POSITION = 0;
    public static final int STARRED_POSITION = 1;
    public static final int QUIZ_POSITION = 2;

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
    public int getLayoutResourceId() {
        return R.layout.activity_category;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int position = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt(EXTRA_VIEW_POSITION);
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        CategoryFragment categoryFragment = CategoryFragment.newInstance();
        StarredFragment starredFragment = StarredFragment.newInstance();
        QuizFragment quizFragment = QuizFragment.newInstance();
        adapter.addFragment(categoryFragment);
        adapter.addFragment(starredFragment);
        adapter.addFragment(quizFragment);
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
}
