package com.nakhmedov.finance.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.db.entity.DaoSession;
import com.nakhmedov.finance.db.entity.RecentSearches;
import com.nakhmedov.finance.db.entity.RecentSearchesDao;
import com.nakhmedov.finance.db.entity.Term;
import com.nakhmedov.finance.db.entity.TermDao;
import com.nakhmedov.finance.net.FinanceHttpService;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.adapter.SearchAdapter;
import com.nakhmedov.finance.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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
 * Date: 4/19/17
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates
 */

public class SearchActivity extends BaseActivity {

    private String TAG = SearchActivity.class.getCanonicalName();

    @BindView(R.id.search_view) SearchView mSearchView;
    @BindView(R.id.searchRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.empty_view) View emptyView;

    private SearchAdapter mSearchAdapter;
    private DaoSession daoSession;

    @Override
    public int getLayoutResourceId() {
        return R.layout.search_layout;
    }

    public interface OnSearchSubmitListener {
        void onSubmit(String searchTermName);
        void onSetText(String text);
        void onSelectedTerm(Term selectedTerm);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showBackBtn();

        daoSession = ((FinanceApp) getApplicationContext()).getDaoSession();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mSearchAdapter = new SearchAdapter(SearchActivity.this, mListener);
        mRecyclerView.setAdapter(mSearchAdapter);

        showUserSearchHistory();
    }

    private void showUserSearchHistory() {
        List<RecentSearches> histories = daoSession
                .getRecentSearchesDao()
                .loadAll();
        mSearchAdapter.swapData(histories);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchView.setIconified(false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String termName) {
                doSearch(termName, true);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String termName) {
                displayHideEmptyView(1);
                if (termName.isEmpty()) {
                    showUserSearchHistory();
                } else {
                    doSearch(termName, false);
                }

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private OnSearchSubmitListener mListener = new OnSearchSubmitListener() {
        @Override
        public void onSubmit(String searchTermName) {
            doSearch(searchTermName, false);
        }

        @Override
        public void onSetText(String text) {
            mSearchView.setQuery(text, true);
        }

        @Override
        public void onSelectedTerm(Term selectedTerm) {
            saveSelectedTerm(selectedTerm);
            displaySelectedTerm(selectedTerm);
        }
    };

    private void saveSelectedTerm(Term selectedTerm) {
        Log.i(TAG, "Saved to RECENT = " + selectedTerm.getName());

        RecentSearches searches = new RecentSearches(selectedTerm.getName());
        int count = (int) daoSession
                .getRecentSearchesDao()
                .queryBuilder()
                .where(RecentSearchesDao.Properties.RecentTermName.like(searches.getRecentTermName().toLowerCase()))
                .count();
        if (count == 0) {
            daoSession
                    .getRecentSearchesDao()
                    .insert(searches);
        }
    }

    private void displaySelectedTerm(Term selectedTerm) {
        Intent intent = new Intent(SearchActivity.this, SelectedTermActivity.class);
        intent.putExtra(SelectedTermActivity.EXTRA_TERM_ID, selectedTerm.getId());
        intent.putExtra(SelectedTermActivity.EXTRA_CATEGORY_ID, selectedTerm.getCategoryId());
        startActivity(intent);

    }

    private void doSearch(String searchTermName, boolean isSubmitted) {
        if (searchTermName.isEmpty()) {
            return;
        }
        showDialog();
        String query = "%" + searchTermName + "%";
        List<Term> possibleTerms = daoSession
                .getTermDao()
                .queryBuilder()
                .where(TermDao.Properties.Name.like(query))
                .build()
                .list();
        Log.i(TAG, "doSearch query = " + query + " ; result = " + possibleTerms.size());

        if (isSubmitted) {
            doRemoteSearch(searchTermName);
            return;
        }

        List<RecentSearches> list = new ArrayList(possibleTerms);
        mSearchAdapter.swapData(list);
        hideDialog();

    }

    private void doRemoteSearch(String termName) {
        if (!NetworkUtil.isNetActive(SearchActivity.this)) {
            hideDialog();
            showMessage(getString(R.string.no_internet));
            return;
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContextConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        FinanceHttpService httpService = retrofit.create(FinanceHttpService.class);
        httpService.doSearch(termName).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i(TAG, "doRemoteSearch onResponse = " + response.toString());
                if (response.isSuccessful()) {
                    doProcess(response);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                Log.i(TAG, "doRemoteSearch onFailure = " + throwable.getMessage());
                throwable.printStackTrace();
                hideDialog();
                displayHideEmptyView(0);
            }
        });

    }

    private void showMessage(String msgText) {
        Snackbar.make(mRecyclerView, msgText, Snackbar.LENGTH_SHORT).show();
    }

    private void doProcess(final Response<JsonArray> response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JsonArray result = response.body();
                final List<Term> termList = new ArrayList<>(result.size());
                for (int i = 0; i < result.size(); i++) {
                    Gson gsonObj = new Gson();
                    Term term = gsonObj.fromJson(result.get(i), Term.class);
                    Term localTerm = daoSession
                            .getTermDao()
                            .queryBuilder()
                            .where(TermDao.Properties.Id.eq(term.getId()))
                            .unique();
                    if (localTerm == null) {
                        daoSession
                                .getTermDao()
                                .insert(term);
                        termList.add(term);
                    } else {
                        termList.add(localTerm);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<RecentSearches> list = new ArrayList(termList);
                        mSearchAdapter.swapData(list);
                        hideDialog();
                        displayHideEmptyView(termList.size());
                    }
                });
            }
        }).start();
    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void displayHideEmptyView(int size) {
        emptyView.setVisibility(size == 0 ? View.VISIBLE : View.INVISIBLE);
    }

}
