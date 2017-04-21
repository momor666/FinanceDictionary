package com.nakhmedov.finance.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.constants.PrefLab;
import com.nakhmedov.finance.net.FinanceHttpService;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.activity.SelectedCategoryActivity;
import com.nakhmedov.finance.ui.activity.SelectedTermActivity;
import com.nakhmedov.finance.ui.entity.DaoSession;
import com.nakhmedov.finance.ui.entity.Term;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/14/17
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates
 */

public class ViewTermContent extends Fragment {

    private static final String TAG = ViewTermContent.class.getCanonicalName();

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout termView;
    @BindView(R.id.definition_view) TextView definitionView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
//    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.fabtoolbar_fab) FloatingActionButton fab;
    @BindView(R.id.fabtoolbar) FABToolbarLayout view;
    @BindView(R.id.like_white_btn) LikeButton likeButton;

    private static final String TERM_ID = "term_id";
    private DaoSession daoSession;
    private static TextToSpeech mTTS;
    private static MenuItem playItemView;
    private Unbinder unbinder;
    private Term currentTerm;

    public static ViewTermContent newInstance(Long termId) {
        ViewTermContent content = new ViewTermContent();
        Bundle bundle = new Bundle();
        bundle.putLong(TERM_ID, termId);
        content.setArguments(bundle);
        return content;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        daoSession = FinanceApp.getApplication(getContext())
                .getDaoSession();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            long termId = bundle.getLong(TERM_ID);
            currentTerm = daoSession
                    .getTermDao()
                    .load(termId);
            getTermContent(currentTerm);
        }

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Toast.makeText(getContext(), getString(R.string.add_favourite), Toast.LENGTH_SHORT).show();

                currentTerm.setStarred(true);
                daoSession.getTermDao()
                        .update(currentTerm);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                currentTerm.setStarred(false);
                daoSession.getTermDao()
                        .update(currentTerm);
            }
        });

        likeButton.setLiked(currentTerm.getStarred());

        mTTS = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "onDone", Toast.LENGTH_SHORT).show();
                                    if (playItemView != null) {
                                        playItemView.setIcon(R.drawable.ic_play_arrow_white);
                                        playItemView.setChecked(false);
                                    }


                                }
                            });
                        }

                        @Override
                        public void onError(String utteranceId) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), getString(R.string.smth_wrong), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                    String language = ((SelectedTermActivity) getActivity())
                            .prefs.getString(PrefLab.CHOOSE_SPEECH_LANGUAGE, getString(R.string.english_us));
                    Locale locale = Locale.UK;
                    if (language.equals(getString(R.string.english_us))) {
                        locale = Locale.US;
                    }

                    mTTS.setLanguage(locale);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        pauseSpeech(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_term_content, menu);
        playItemView = menu.findItem(R.id.action_play);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
                break;
            }
            case R.id.action_play: {
                boolean isChecked = !item.isChecked();
                item.setChecked(isChecked);
                if (isChecked) {
                    playDefinition();
                } else {
                    pauseSpeech(false);
                    item.setIcon(R.drawable.ic_play_arrow_white);
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseSpeech(true);
    }

    @OnClick(R.id.fabtoolbar_fab)
    public void displayBottomView() {
        view.show();
    }

    @OnClick(R.id.share_term)
    public void shareTerm() {
        view.hide();
        ((SelectedTermActivity) getActivity()).shareViaApp(currentTerm.getName() +
                "\n" + getString(R.string.definition) + "\n" + currentTerm.getDescription());

    }

    @OnClick(R.id.copy_term)
    public void copyTerm() {
        view.hide();
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Label", currentTerm.getDescription());
        clipboard.setPrimaryClip(clip);

    }

    @OnClick(R.id.print_term)
    public void printTerm() {
        view.hide();
    }

    @OnClick(R.id.edit_term)
    public void editTerm() {
        view.hide();
    }

    public void pauseSpeech(boolean needRelease) {
        if (mTTS != null) {
            if (mTTS.isSpeaking()) {
                mTTS.stop();
            }
            if (needRelease) {
                mTTS.shutdown();
            }
        }
    }

    private void playDefinition() {
        if (currentTerm.getDescription() == null) {
            Log.e(TAG, "Term description is null no need to play");
            showMessage(R.string.no_content_play);
            return;
        }

        setPlayToggleIcon(R.drawable.ic_pause_white);

        String uniqueId = UUID.randomUUID().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTTS.speak(currentTerm.getName(), TextToSpeech.QUEUE_ADD, null, null);
            mTTS.speak(getString(R.string.definition), TextToSpeech.QUEUE_ADD, null, null);
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uniqueId);
            mTTS.speak(currentTerm.getDescription(), TextToSpeech.QUEUE_ADD, params, uniqueId);
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uniqueId);

            mTTS.speak(currentTerm.getName(), TextToSpeech.QUEUE_ADD, null);
            mTTS.speak(getString(R.string.definition), TextToSpeech.QUEUE_ADD, null);
            mTTS.speak(currentTerm.getDescription(), TextToSpeech.QUEUE_ADD, params);
        }


    }

    private void showMessage(int msgText) {
        Toast.makeText(getContext(), msgText,  Toast.LENGTH_LONG).show();
    }

    private void setPlayToggleIcon(int icon) {
        if (playItemView != null) {
            playItemView.setIcon(icon);
        }
    }

    private void getTermContent(Term term) {

        displayTermTitle(term);
        if (term.getDescription() == null) {
            Log.w(TAG, "Term description is null, send request to server termName - " + term.getName());
            showLoading();
            sendRequest2Content(term);
        } else {
            displayTermContent(term);
        }
    }

    private void sendRequest2Content(Term currentTerm) {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(ContextConstants.BASE_URL)
                .build();

        FinanceHttpService service = retrofit.create(FinanceHttpService.class);

        service.getTermContent(currentTerm.getId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject result = response.body();
                Gson gsonObj = new Gson();
                Term term = gsonObj.fromJson(result, Term.class);
                daoSession
                        .getTermDao()
                        .update(term);

                displayTermContent(term);
                hideLoading();
                Log.i(TAG, "response success termId" + term.getId());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                throwable.printStackTrace();
                hideLoading();
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void displayTermTitle(Term term) {
        termView.setTitle(term.getName());
    }

    private void displayTermContent(Term term) {
        definitionView.setText(term.getDescription());
    }
}
