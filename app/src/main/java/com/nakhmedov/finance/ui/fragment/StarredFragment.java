package com.nakhmedov.finance.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.db.entity.DaoSession;
import com.nakhmedov.finance.db.entity.Term;
import com.nakhmedov.finance.db.entity.TermDao;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.activity.CategoryActivity;
import com.nakhmedov.finance.ui.activity.SearchActivity;
import com.nakhmedov.finance.ui.activity.SelectedTermActivity;
import com.nakhmedov.finance.ui.activity.SettingsActivity;
import com.nakhmedov.finance.ui.adapter.StarredAdapter;
import com.nakhmedov.finance.ui.components.EmptyRecyclerView;

import java.util.List;

import butterknife.BindView;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/4/17
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates
 */

public class StarredFragment extends BaseFragment {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recyclerview) EmptyRecyclerView recyclerView;
    @BindView(R.id.empty_view) RelativeLayout emptyView;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;

    private StarredAdapter mAdapter;

    public static StarredFragment newInstance() {
        return new StarredFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_starred;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((CategoryActivity) getActivity()).setSupportActionBar(mToolbar);
        ((CategoryActivity) getActivity()).setToolbarTitle(getString(R.string.favourites));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyView);
        mAdapter = new StarredAdapter(getContext(), listener);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoading();
        getStarredData();
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

    private void getStarredData() {
        DaoSession daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();
        List<Term> termList = daoSession
                .getTermDao()
                .queryBuilder()
                .where(TermDao.Properties.Starred.eq(true))
                .build()
                .list();
        appBarLayout.setExpanded(termList.size() != 0);
        updateUI(termList);
    }

    private void updateUI(List<Term> termList) {
        mAdapter.setData(termList);
        hideLoading();
    }

    private OnStarredItemClickListener listener = new OnStarredItemClickListener() {
        @Override
        public void onItemClick(long termId, long categoryId) {
            Intent intent = new Intent(getActivity(), SelectedTermActivity.class);
            intent.putExtra(SelectedTermActivity.EXTRA_TERM_ID, termId);
            intent.putExtra(SelectedTermActivity.EXTRA_CATEGORY_ID, categoryId);
            startActivity(intent);
        }
    };


    public interface OnStarredItemClickListener {
        void onItemClick(long termId, long categoryId);
    }
}
