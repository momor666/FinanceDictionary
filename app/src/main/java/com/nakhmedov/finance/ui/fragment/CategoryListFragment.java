package com.nakhmedov.finance.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ActionNames;
import com.nakhmedov.finance.constants.ExtrasNames;
import com.nakhmedov.finance.constants.PrefLab;
import com.nakhmedov.finance.db.entity.Category;
import com.nakhmedov.finance.db.entity.CategoryDao;
import com.nakhmedov.finance.db.entity.DaoSession;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.activity.BaseActivity;
import com.nakhmedov.finance.ui.activity.SelectedCategoryActivity;
import com.nakhmedov.finance.ui.adapter.CategoryAdapter;
import com.nakhmedov.finance.ui.components.EmptyRecyclerView;
import com.nakhmedov.finance.ui.listener.OnItemClickListener;
import com.nakhmedov.finance.ui.service.CategoryUpdateService;
import com.nakhmedov.finance.util.AndroidUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/4/17
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates
 */

public class CategoryListFragment extends BaseFragment {
    private String TAG = CategoryListFragment.class.getCanonicalName();

    @BindView(R.id.recyclerview) EmptyRecyclerView recyclerView;
    @BindView(R.id.empty_view) FrameLayout emptyView;

    private static final String KEY_POSITION = "position";
    public static final int ALL = 0;
    public static final int STARRED = 1;
    private CategoryAdapter mAdapter;

    public static Fragment newInstance(int position) {
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_category_list;
    }

    @Override
    public void onResume() {
        super.onResume();
        int position = getArguments().getInt(KEY_POSITION, -1);

        if (position == STARRED) {
            getStarredData();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyView);

        mAdapter = new CategoryAdapter(getActivity(), listener);
        recyclerView.setAdapter(mAdapter);

        int position = getArguments().getInt(KEY_POSITION, -1);

        if (position == ALL) {
            IntentFilter intentFilter = new IntentFilter(ActionNames.UPDATE_CATEGORY_LIST);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(categoryReceiver, intentFilter);

            getOrUpdateData(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(categoryReceiver);
    }

    private void getOrUpdateData(boolean withService) {
        if (isAdded()) { // Service is sent to Intent via Broadcast
            showLoading();
            final DaoSession daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();
            final List<Category> localCategoryList = daoSession
                    .getCategoryDao()
                    .queryBuilder()
                    .orderAsc(CategoryDao.Properties.Name)
                    .build()
                    .list();

            updateUI(localCategoryList);

            long lastUpdateTime = ((BaseActivity) getActivity()).prefs.getLong(PrefLab.CATEGORY_LAST_UPDATE, 0);
            int updatePeriodInDay = ((BaseActivity) getActivity()).prefs.getInt(PrefLab.UPDATE_DATA_PERIOD, 7);

            if (withService && (lastUpdateTime == 0 || AndroidUtil.isMoreThanSelectedDays(new Date(lastUpdateTime), updatePeriodInDay))) {
                showLoading();
                getActivity().startService(new Intent(getContext(), CategoryUpdateService.class));
            } else {
                Log.w(TAG, "withService = " + withService + " lastUpdateTime = " + lastUpdateTime);
            }
        }
    }

    private void getStarredData() {
        showLoading();
        DaoSession daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();
        List<Category> starredCategoryList = daoSession
                .getCategoryDao()
                .queryBuilder()
                .where(CategoryDao.Properties.Starred.eq(true))
                .build()
                .list();
        updateUI(starredCategoryList);
    }

    private void updateUI(List<Category> list) {
        mAdapter.setData(list);
        hideLoading();
    }

    private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(Category selectedCategory) {
            Intent intent = new Intent(getActivity(), SelectedCategoryActivity.class);
            intent.putExtra(SelectedCategoryActivity.EXTRA_NAME, selectedCategory.getName());
            intent.putExtra(SelectedCategoryActivity.EXTRA_CATEGORY_ID, selectedCategory.getId());
            startActivity(intent);
        }
    };

    private BroadcastReceiver categoryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
            if (intent.getAction().equals(ActionNames.UPDATE_CATEGORY_LIST)) {
                hideLoading();
                if (intent.getExtras().getBoolean(ExtrasNames.CATEGORY_UPDATE_RESULT)) {
                    getOrUpdateData(false);
                } else {
                    Snackbar.make(recyclerView, getString(R.string.category_update_failed), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getOrUpdateData(true);
                                }
                            })
                            .show();
                }
            }
        }
    };

    private void showMessage(String text) {
        Snackbar.make(recyclerView, text, Snackbar.LENGTH_SHORT).show();
    }

}
