package com.nakhmedov.finance.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.adapter.CategoryAdapter;
import com.nakhmedov.finance.ui.entity.Category;
import com.nakhmedov.finance.ui.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/4/17
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates
 */

public class CategoryListFragment extends Fragment {

    @BindView(R.id.recyclerview) RecyclerView recyclerView;

    private List<Category> categoryList = new ArrayList<>(15);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_category_list, container, false);
        setupAdapter(recyclerView);
        return recyclerView;

    }

    private void setupAdapter(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        categoryList.add(new Category("Item1"));
        categoryList.add(new Category("Item2"));
        categoryList.add(new Category("Item3"));
        categoryList.add(new Category("Item4"));
        categoryList.add(new Category("Item5"));
        categoryList.add(new Category("Item6"));
        categoryList.add(new Category("Item7"));
        categoryList.add(new Category("Item8"));
        categoryList.add(new Category("Item9"));
        categoryList.add(new Category("Item10"));
        categoryList.add(new Category("Item11"));
        categoryList.add(new Category("Item1"));
        categoryList.add(new Category("Item2"));
        categoryList.add(new Category("Item3"));
        categoryList.add(new Category("Item4"));
        categoryList.add(new Category("Item5"));
        categoryList.add(new Category("Item6"));
        categoryList.add(new Category("Item7"));
        categoryList.add(new Category("Item8"));
        categoryList.add(new Category("Item9"));
        categoryList.add(new Category("Item10"));
        categoryList.add(new Category("Item11"));

        CategoryAdapter mAdapter = new CategoryAdapter(getActivity(), categoryList, listener);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Toast.makeText(getContext(), categoryList.get(position).getName(), Toast.LENGTH_SHORT).show();

        }
    };
}
