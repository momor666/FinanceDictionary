package com.nakhmedov.finance.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ActionNames;
import com.nakhmedov.finance.constants.ExtrasNames;
import com.nakhmedov.finance.constants.PrefLab;
import com.nakhmedov.finance.db.entity.Category;
import com.nakhmedov.finance.db.entity.DaoSession;
import com.nakhmedov.finance.db.entity.Term;
import com.nakhmedov.finance.db.entity.TermDao;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.activity.BaseActivity;
import com.nakhmedov.finance.ui.activity.SearchActivity;
import com.nakhmedov.finance.ui.activity.SelectedCategoryActivity;
import com.nakhmedov.finance.ui.adapter.TermsAdapter;
import com.nakhmedov.finance.ui.service.TermUpdateService;
import com.nakhmedov.finance.util.AndroidUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/6/17
 * Time: 12:02 PM
 * To change this template use File | Settings | File Templates
 */

public class SelectedCategory extends BaseFragment {

    private static final String TAG = SelectedCategory.class.getCanonicalName();

    OnTermSelectedListener mCallback;

    @BindView(R.id.indexable_recycler_view) IndexableLayout indexableLayout;

    public static final String FRAG_TAG = "SelectedCategory";
    private TermsAdapter termsAdapter;
    private long extraCategoryId;

    public interface OnTermSelectedListener {
        void onTermSelected(long termId, long categoryId);
    }

    public static SelectedCategory newInstance(Bundle extras) {
        SelectedCategory selectedCategory = new SelectedCategory();
        selectedCategory.setArguments(extras);
        return selectedCategory;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCallback = (OnTermSelectedListener) context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_selected_category;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        IntentFilter intentFilter = new IntentFilter(ActionNames.UPDATE_TERM_LIST);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(termReceiver, intentFilter);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((SelectedCategoryActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        indexableLayout.setLayoutManager(layoutManager);

        termsAdapter = new TermsAdapter(getContext());
        indexableLayout.setAdapter(termsAdapter);

        // set Datas
//        mAdapter.setDatas(initDatas());
        // set Material Design OverlayView
        indexableLayout.setOverlayStyle_MaterialDesign(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        indexableLayout.setCompareMode(IndexableLayout.MODE_ALL_LETTERS);

        termsAdapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<Term>() {
            @Override
            public void onItemClick(View view, int originalPosition, int currentPosition, Term term) {
                mCallback.onTermSelected(term.getId(), extraCategoryId);
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ((SelectedCategoryActivity) getActivity()).setToolbarTitle(
                    bundle.getString(SelectedCategoryActivity.EXTRA_NAME));

            extraCategoryId = bundle.getLong(SelectedCategoryActivity.EXTRA_CATEGORY_ID);
            loadTermsByCategoryId(extraCategoryId, true);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.selected_category_item, menu);

        final DaoSession daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();

        final Category category = daoSession
                .getCategoryDao()
                .load(extraCategoryId);

        if (category != null) {
            LikeButton likeButton = (LikeButton) menu.findItem(R.id.category_like).getActionView();
            likeButton.setLiked(category.getStarred());
            likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    category.setStarred(true);
                    daoSession.getCategoryDao().update(category);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    category.setStarred(false);
                    daoSession.getCategoryDao().update(category);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.category_share: {
                DaoSession daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();
                Category category = daoSession
                        .getCategoryDao()
                        .load(extraCategoryId);
                ((SelectedCategoryActivity) getActivity()).shareViaApp(getString(R.string.category_sharing_txt, category.getName()));
                break;
            }
            case R.id.action_search: {
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
            }
            default: {

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(termReceiver);
    }

    private void loadTermsByCategoryId(long categoryId, boolean withService) {
        if (isAdded()) {
            showLoading();
            final DaoSession daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();
            final List<Term> termList = daoSession
                    .getTermDao()
                    .queryBuilder()
                    .where(TermDao.Properties.CategoryId.eq(categoryId))
                    .build()
                    .list();
            updateTermsListUI(termList);

            Category category = daoSession.getCategoryDao().load(categoryId);
            int updatePeriodInDay = ((BaseActivity) getActivity()).prefs.getInt(PrefLab.UPDATE_DATA_PERIOD, 7);
            long lastUpdateTime = category.getLastTermsUpdateTime();

            if (withService && (lastUpdateTime == 0 ||
                    AndroidUtil.isMoreThanSelectedDays(new Date(lastUpdateTime), updatePeriodInDay))) {
                showLoading();
                Intent termServiceIntent = new Intent(getContext(), TermUpdateService.class);
                termServiceIntent.putExtra(TermUpdateService.EXTRA_CATEGORY_ID, categoryId);
                getActivity().startService(termServiceIntent);
            }
        }
    }

    private void updateTermsListUI(List<Term> termList) {
        termsAdapter.setDatas(termList);
        hideLoading();
    }

    private BroadcastReceiver termReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
            if (intent.getAction().equals(ActionNames.UPDATE_TERM_LIST)) {
                hideLoading();
                if (intent.getExtras().getBoolean(ExtrasNames.TERM_UPDATE_RESULT)) {
                    long categoryId = intent.getExtras().getLong(TermUpdateService.EXTRA_CATEGORY_ID);
                    loadTermsByCategoryId(categoryId, false);
                } else {
                    Snackbar.make(indexableLayout, getString(R.string.term_update_failed), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadTermsByCategoryId(extraCategoryId, true);
                                }
                            })
                            .show();
                }
            }
        }
    };

    private void showMessage(String msgText) {
        Snackbar.make(indexableLayout, msgText, Snackbar.LENGTH_SHORT).show();
    }

}
