package com.nakhmedov.finance.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.net.FinanceHttpService;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.activity.SelectedCategoryActivity;
import com.nakhmedov.finance.ui.adapter.TermsAdapter;
import com.nakhmedov.finance.ui.entity.Category;
import com.nakhmedov.finance.ui.entity.DaoSession;
import com.nakhmedov.finance.ui.entity.Term;
import com.nakhmedov.finance.ui.entity.TermDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/6/17
 * Time: 12:02 PM
 * To change this template use File | Settings | File Templates
 */

public class SelectedCategory extends Fragment {

    private static final String TAG = SelectedCategory.class.getCanonicalName();

    OnTermSelectedListener mCallback;

    @BindView(R.id.indexable_recycler_view) IndexableLayout indexableLayout;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
//    @BindView(R.id.toolbar) Toolbar mToolbar;

    public static final String FRAG_TAG = "SelectedCategory";
    private Unbinder unbinder;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_selected_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

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
            loadTermsByCategoryId(extraCategoryId);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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

                break;
            }
            default: {

            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadTermsByCategoryId(final long categoryId) {
        showLoading();
        final DaoSession daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();
        final List<Term> termList = daoSession
                .getTermDao()
                .queryBuilder()
                .where(TermDao.Properties.CategoryId.eq(categoryId))
                .build()
                .list();
        if (termList.isEmpty()) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ContextConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

            FinanceHttpService service = retrofit.create(FinanceHttpService.class);
            service.listTermsBy(categoryId).enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    Log.i(TAG, "response = " + response.toString());
                    JsonArray result = response.body();
                    for (int i = 0; i < result.size(); i++) {
                        Gson gsonObj = new Gson();
                        Term term = gsonObj.fromJson(result.get(i), Term.class);
                        daoSession
                                .getTermDao()
                                .insert(term);
                        termList.add(term);
                    }

                    updateTermsListUI(termList);
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable throwable) {

                    hideLoading();
                }
            });
        } else {
            updateTermsListUI(termList);
        }
    }

    private void updateTermsListUI(List<Term> termList) {
        termsAdapter.setDatas(termList);
        hideLoading();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
