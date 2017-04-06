package com.nakhmedov.finance.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.activity.CategoryActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/2/17
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates
 */

public class CategoryFragment extends Fragment {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.category_tabs) TabLayout tabLayout;


    private static CategoryFragment categoryListFragment;

    public static CategoryFragment getInstance() {
        if (categoryListFragment == null) {
            categoryListFragment = new CategoryFragment();
        }

        return categoryListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new CategoryListFragment(), getString(R.string.all));
        adapter.addFragment(new CategoryListFragment(), getString(R.string.starred));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        ((CategoryActivity) getActivity()).setSupportActionBar(mToolbar);
        ((CategoryActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((CategoryActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.categories));


    }

    private class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>(2);
        private final List<String> mFragmentTitles = new ArrayList<>(2);

        private Adapter(FragmentManager fm) {
            super(fm);
        }

        private void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
