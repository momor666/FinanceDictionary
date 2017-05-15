package com.nakhmedov.finance.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.activity.CategoryActivity;
import com.nakhmedov.finance.ui.activity.SearchActivity;
import com.nakhmedov.finance.ui.activity.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/2/17
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates
 */

public class CategoryFragment extends BaseFragment {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.category_tabs) TabLayout tabLayout;

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(CategoryListFragment.newInstance(CategoryListFragment.ALL), getString(R.string.all));
        adapter.addFragment(CategoryListFragment.newInstance(CategoryListFragment.STARRED), getString(R.string.favourites));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        ((CategoryActivity) getActivity()).setSupportActionBar(mToolbar);
        ((CategoryActivity) getActivity()).setToolbarTitle(getString(R.string.categories));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            }
            case R.id.action_settings: {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            }

        }
        return super.onOptionsItemSelected(item);
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
