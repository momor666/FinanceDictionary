package com.nakhmedov.finance.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.activity.CategoryActivity;
import com.nakhmedov.finance.ui.adapter.StarredAdapter;
import com.nakhmedov.finance.ui.entity.Term;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/4/17
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates
 */

public class StarredFragment extends Fragment {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recyclerview) RecyclerView recyclerView;

    private static StarredFragment starredFragment;

    public static StarredFragment getInstance() {
        if (starredFragment == null) {
            starredFragment = new StarredFragment();
        }

        return starredFragment;
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

        ButterKnife.bind(this, view);

        ((CategoryActivity) getActivity()).setSupportActionBar(mToolbar);
        ((CategoryActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((CategoryActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.starred));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        List<Term> starredTermList = new ArrayList<>(10);
        starredTermList.add(new Term("Term1", "definition1"));
        starredTermList.add(new Term("Term2", "definition2"));
        starredTermList.add(new Term("Term3", "definition3"));
        starredTermList.add(new Term("Term4", "definition4"));
        starredTermList.add(new Term("Term5", "definition5"));
        starredTermList.add(new Term("Term6", "definition6"));
        starredTermList.add(new Term("Term7", "definition7"));
        starredTermList.add(new Term("Term8", "definition8"));
        starredTermList.add(new Term("Term9", "definition9"));
        starredTermList.add(new Term("Term10", "definition10"));
        RecyclerView.Adapter mAdapter = new StarredAdapter(getContext(), starredTermList);
        recyclerView.setAdapter(mAdapter);
    }
}
