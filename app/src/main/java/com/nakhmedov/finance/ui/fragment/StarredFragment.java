package com.nakhmedov.finance.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.activity.CategoryActivity;
import com.nakhmedov.finance.ui.activity.SelectedCategoryActivity;
import com.nakhmedov.finance.ui.activity.SelectedTermActivity;
import com.nakhmedov.finance.ui.adapter.StarredAdapter;
import com.nakhmedov.finance.ui.components.EmptyRecyclerView;
import com.nakhmedov.finance.ui.entity.DaoSession;
import com.nakhmedov.finance.ui.entity.Term;
import com.nakhmedov.finance.ui.entity.TermDao;
import com.nakhmedov.finance.ui.listener.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/4/17
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates
 */

public class StarredFragment extends Fragment {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recyclerview) EmptyRecyclerView recyclerView;
    @BindView(R.id.empty_view) FrameLayout emptyView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;


    private Unbinder unbinder;
    private StarredAdapter mAdapter;

    public static StarredFragment newInstance() {
        return new StarredFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_starred, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        ((CategoryActivity) getActivity()).setSupportActionBar(mToolbar);
        ((CategoryActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.starred));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyView);
        mAdapter = new StarredAdapter(getContext(), listener);
        recyclerView.setAdapter(mAdapter);

        showLoading();
        getStarredData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void getStarredData() {
        DaoSession daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();
        List<Term> termList = daoSession
                .getTermDao()
                .queryBuilder()
                .where(TermDao.Properties.Starred.eq(true))
                .build()
                .list();
        updateUI(termList);
    }

    private void updateUI(List<Term> termList) {
        mAdapter.setData(termList);
        hideLoading();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
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
