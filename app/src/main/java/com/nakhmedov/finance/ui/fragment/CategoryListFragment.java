package com.nakhmedov.finance.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.net.FinanceHttpService;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.activity.SelectedCategoryActivity;
import com.nakhmedov.finance.ui.adapter.CategoryAdapter;
import com.nakhmedov.finance.ui.components.EmptyRecyclerView;
import com.nakhmedov.finance.ui.entity.Category;
import com.nakhmedov.finance.ui.entity.CategoryDao;
import com.nakhmedov.finance.ui.entity.DaoSession;
import com.nakhmedov.finance.ui.listener.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
 * Date: 4/4/17
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates
 */

public class CategoryListFragment extends Fragment {

    private static final String KEY_POSITION = "position";
    public static final int ALL = 0;
    public static final int STARRED = 1;
    private String TAG = CategoryListFragment.class.getCanonicalName();

    @BindView(R.id.recyclerview) EmptyRecyclerView recyclerView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.empty_view) FrameLayout emptyView;

    private Unbinder unbinder;
    private CategoryAdapter mAdapter;
    private List<Category> categoryList;

    public static Fragment newInstance(int position) {
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyView);

        mAdapter = new CategoryAdapter(getActivity(), listener);
        recyclerView.setAdapter(mAdapter);

        int position = getArguments().getInt(KEY_POSITION, -1);

        showLoading();
        if (position == ALL) {
            getOrUpdateData();
        } else {
            getStarredData();
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void getOrUpdateData() {
        final DaoSession daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();
        categoryList = daoSession
                .getCategoryDao()
                .loadAll();

        if (categoryList.isEmpty()) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            final Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ContextConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
            FinanceHttpService httpService = retrofit.create(FinanceHttpService.class);
            httpService.listCategory().enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    Log.i(TAG, "response = " + response.toString());
                    JsonArray result = response.body();
                    for (int i = 0; i < result.size(); i++) {
                        Gson gsonObj = new Gson();
                        Category category = gsonObj.fromJson(result.get(i), Category.class);
                        daoSession
                                .getCategoryDao()
                                .insert(category);
                        categoryList.add(category);

                    }
                    updateUI(categoryList);
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable throwable) {
                    Log.e(TAG, "failed = " + throwable.getMessage());
                    updateUI(categoryList);
                    throwable.printStackTrace();
                }
            });
        } else {
            updateUI(categoryList);
        }
    }

    private void getStarredData() {
        DaoSession daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();
        categoryList = daoSession
                .getCategoryDao()
                .queryBuilder()
                .where(CategoryDao.Properties.Starred.eq(true))
                .build()
                .list();
        updateUI(categoryList);
    }

    private void updateUI(List<Category> categoryList) {
        mAdapter.setData(categoryList);
        hideLoading();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(getActivity(), SelectedCategoryActivity.class);
            intent.putExtra(SelectedCategoryActivity.EXTRA_NAME, categoryList.get(position).getName());
            intent.putExtra(SelectedCategoryActivity.EXTRA_CATEGORY_ID, categoryList.get(position).getId());
            startActivity(intent);
        }
    };
}
