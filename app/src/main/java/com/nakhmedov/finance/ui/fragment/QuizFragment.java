package com.nakhmedov.finance.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.db.entity.Category;
import com.nakhmedov.finance.db.entity.CategoryDao;
import com.nakhmedov.finance.db.entity.DaoSession;
import com.nakhmedov.finance.db.entity.Quiz;
import com.nakhmedov.finance.db.entity.Term;
import com.nakhmedov.finance.db.entity.TermDao;
import com.nakhmedov.finance.net.FinanceHttpService;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.activity.BaseActivity;
import com.nakhmedov.finance.ui.activity.CategoryActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
 * Date: 4/4/17
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates
 */

public class QuizFragment extends BaseFragment implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, View.OnClickListener {

    private static final String TAG = QuizFragment.class.getCanonicalName();
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.questionTextView) TextView mQuestionTextView;
    @BindView(R.id.first_option_btn) Button mFirstOptBtn;
    @BindView(R.id.second_option_btn) Button mSecondOptBtn;
    @BindView(R.id.third_option_btn) Button mThirdOptBtn;
    @BindView(R.id.fourth_option_btn) Button mFourthOptBtn;

    private int currentQuestionIndex = -1;
    private AudioManager audioManager;
    private DaoSession daoSession;
    private boolean isDialogShowing;
    private List<Quiz> quizList;
    private int attempt = 0;
    private ArrayList<String> distractorList;
    private ArrayList<Term> questionTermList;


    public static QuizFragment newInstance() {
        return new QuizFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_quiz;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        ((CategoryActivity) getActivity()).setSupportActionBar(mToolbar);
        ((CategoryActivity) getActivity()).setToolbarTitle(getString(R.string.quiz));

        mQuestionTextView.setMovementMethod(new ScrollingMovementMethod());
        mFirstOptBtn.setOnClickListener(this);
        mSecondOptBtn.setOnClickListener(this);
        mThirdOptBtn.setOnClickListener(this);
        mFourthOptBtn.setOnClickListener(this);

        daoSession = ((FinanceApp) getActivity().getApplicationContext()).getDaoSession();

        if (savedInstanceState != null) {
            currentQuestionIndex = -1;
        }
        loadQuizs();
    }

    @Override
    public void onClick(View view) {
        if (quizList == null || quizList.size() == 0) {
            Log.i(TAG, "quizList = " + quizList);
            return;
        }
        Quiz currentQuiz = quizList.get(currentQuestionIndex);
        Button clickedBtn = (Button) view;
        attempt++;
        if (clickedBtn.getText().equals(currentQuiz.getAnswer())) {
            play("android.resource://" + ContextConstants.PACKAGE_NAME + "/" + R.raw.sound_correct);
            clickedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            currentQuiz.setAttempts(attempt);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayNextQuestion();
                }
            }, 1000);
        } else {
            play("android.resource://" + ContextConstants.PACKAGE_NAME + "/" + R.raw.sound_error);
            clickedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));
        }
    }

    private void loadQuizs() {
        List<Category> starredCategories = daoSession
                .getCategoryDao()
                .queryBuilder()
                .where(CategoryDao.Properties.Starred.eq(true))
                .list();
        List<Term> termList = new ArrayList<>(1000);
        questionTermList = new ArrayList<>(10);
        distractorList = new ArrayList<>(15);
        if (starredCategories.size() == 0) {
            showDialog(getString(R.string.need_faovourite_category), false);
            return;
        }
        for (Category starredCategory: starredCategories) {
            termList.addAll(
                    daoSession
                            .getTermDao()
                            .queryBuilder()
                            .where(TermDao.Properties.CategoryId.eq(starredCategory.getId()))
                            .list()
            );
        }

        Collections.shuffle(termList);
        if (termList.size() > 0) {
            for (int i = 0; i < 5; i++) {
                questionTermList.add(termList.get(i));
            }
            for (int i = 5; i < 20; i++) {
                distractorList.add(termList.get(i).getName());
            }
        }

        int size = questionTermList.size();
        for (int i = 0; i < size; i++) {
            Term questionTerm = questionTermList.get(i);
            showLoading();
            sendRequest2Content(questionTerm, i == size-1);
        }
    }

    private void displayNextQuestion() {
        attempt = 0;
        if (currentQuestionIndex == quizList.size() - 1) {
            showDialog(getString(R.string.finish_test), true);
            return;
        }
        currentQuestionIndex++;
        Quiz currentQuiz = quizList.get(currentQuestionIndex);
        mQuestionTextView.setText(currentQuiz.getQustionText());
        Random random = new Random();
        switch (random.nextInt(4)) {
            case 0: {
                mFirstOptBtn.setText(currentQuiz.getAnswer());
                setOtherOptions(0);
                break;
            }
            case 1: {
                mSecondOptBtn.setText(currentQuiz.getAnswer());
                setOtherOptions(1);
                break;
            }
            case 2: {
                mThirdOptBtn.setText(currentQuiz.getAnswer());
                setOtherOptions(2);
                break;
            }
            case 3: {
                mFourthOptBtn.setText(currentQuiz.getAnswer());
                setOtherOptions(3);
                break;
            }
            default: {
                mFirstOptBtn.setText(currentQuiz.getAnswer());
                setOtherOptions(0);
            }
        }

        resetOptionsColor();
    }

    private void setOtherOptions(int correctAnswerIndex) {
        String firstDistractorText = distractorList.get(10 - currentQuestionIndex);
        String secondDistractorText = distractorList.get(11 - currentQuestionIndex);
        String thirdDistractorText = distractorList.get(12 - currentQuestionIndex);
        switch (correctAnswerIndex) {
            case 0: {
                mSecondOptBtn.setText(firstDistractorText);
                mThirdOptBtn.setText(secondDistractorText);
                mFourthOptBtn.setText(thirdDistractorText);
                break;
            }
            case 1: {
                mFirstOptBtn.setText(firstDistractorText);
                mThirdOptBtn.setText(secondDistractorText);
                mFourthOptBtn.setText(thirdDistractorText);
                break;
            }
            case 2: {
                mFirstOptBtn.setText(firstDistractorText);
                mSecondOptBtn.setText(secondDistractorText);
                mFourthOptBtn.setText(thirdDistractorText);
                break;
            }
            case 3: {
                mFirstOptBtn.setText(firstDistractorText);
                mSecondOptBtn.setText(secondDistractorText);
                mThirdOptBtn.setText(thirdDistractorText);
                break;
            }
        }
    }

    private void resetOptionsColor() {
        int defaultColor = ContextCompat.getColor(getContext(), R.color.black_transparent);
        mFirstOptBtn.setBackgroundColor(defaultColor);
        mSecondOptBtn.setBackgroundColor(defaultColor);
        mThirdOptBtn.setBackgroundColor(defaultColor);
        mFourthOptBtn.setBackgroundColor(defaultColor);
    }

    private void play(String soundPath) {
        int mode = audioManager.getRingerMode();
        if (mode == AudioManager.RINGER_MODE_SILENT || mode == AudioManager.RINGER_MODE_VIBRATE) {
            return;
        }
        //soundPath = android.resource://com.nakhmedov.finance/2131099660
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getContext(), Uri.parse(soundPath));
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    private void sendRequest2Content(final Term currentQuestionTerm, final boolean lastRequest) {
        if (currentQuestionTerm.getDescription() == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(ContextConstants.BASE_URL)
                    .client(client)
                    .build();

            FinanceHttpService service = retrofit.create(FinanceHttpService.class);

            service.getTermContent(currentQuestionTerm.getId()).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.isSuccessful()) {
                                JsonObject result = response.body();
                                Gson gsonObj = new Gson();
                                Term term = gsonObj.fromJson(result, Term.class);
                                currentQuestionTerm.setDescription(term.getDescription());
                                daoSession
                                        .getTermDao()
                                        .update(currentQuestionTerm);
                                if (lastRequest) {
                                    doQuizList();
                                }
                            }

                        }
                    }).start();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    throwable.printStackTrace();
                    if (isAdded()) {
                        if (!isDialogShowing) {
                            showDialog(getString(R.string.cant_get_content), false);
                        }
                        hideLoading();
                    }
                }
            });
        } else if (lastRequest) {
            doQuizList();
        }
    }

    private void doQuizList() {
        quizList = new ArrayList<>(questionTermList.size());
        for (Term questionTerm : questionTermList) {
            Quiz quiz = new Quiz(questionTerm.getDescription(), questionTerm.getName());
            quizList.add(quiz);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayNextQuestion();
                hideLoading();
            }
        });
    }

    private void showDialog(String msgText, boolean needBarChart) {
        if (getUserVisibleHint()) {
            isDialogShowing = true;
            final Dialog alertDialog = new Dialog(getActivity());
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setTitle(msgText);
            alertDialog.setContentView(R.layout.dialog_content);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            TextView mMsgTextView = (TextView) alertDialog.findViewById(R.id.msgTextView);
            ImageView mRestartQuizView = (ImageView) alertDialog.findViewById(R.id.restart_quiz);
            ImageView mBackQuizView = (ImageView) alertDialog.findViewById(R.id.back_quiz);
            BarChart mChart = (BarChart) alertDialog.findViewById(R.id.chart1);
            mMsgTextView.setText(msgText);
            mRestartQuizView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentQuestionIndex = -1;
                    loadQuizs();
                    alertDialog.dismiss();
                    isDialogShowing = false;
                    ((BaseActivity) getActivity()).requestNewInterstitial();

                }
            });
            mBackQuizView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CategoryActivity) getActivity()).showCategoryPage();
                    alertDialog.dismiss();
                    isDialogShowing = false;
                    currentQuestionIndex = -1;
                    ((BaseActivity) getActivity()).requestNewInterstitial();
                }
            });
            if (needBarChart) {
                drawChart(mChart);
            }

            alertDialog.show();
        }
    }

    private void drawChart(BarChart mChart) {
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);

        int firstAttemptCount = 0;
        int secondAttemptCount = 0;
        int thirdAttemptCount = 0;
        int wrongAttemptCount = 0;
        for (Quiz quiz: quizList) {
            int attempts = quiz.getAttempts();
            if (attempts == 1) {
                firstAttemptCount++;
            } else if (attempts == 2) {
                secondAttemptCount++;
            } else if (attempts == 3) {
                thirdAttemptCount++;
            } else if (attempts == 4) {
                wrongAttemptCount++;
            }
        }

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        List<BarEntry> firstEntries = new ArrayList<>(5);
        List<BarEntry> secondEntries = new ArrayList<>(5);
        List<BarEntry> thirdEntries = new ArrayList<>(5);
        List<BarEntry> wrongEntries = new ArrayList<>(5);
        firstEntries.add(new BarEntry(0f, firstAttemptCount));
        secondEntries.add(new BarEntry(1f, secondAttemptCount));
        thirdEntries.add(new BarEntry(2f, thirdAttemptCount));
        wrongEntries.add(new BarEntry(3f, wrongAttemptCount));

        BarDataSet firstDataSet = new BarDataSet(firstEntries, "First");
        BarDataSet secondDataSet = new BarDataSet(secondEntries, "Second");
        BarDataSet thirdDataSet = new BarDataSet(thirdEntries, "Third");
        BarDataSet wrongDataSet = new BarDataSet(wrongEntries, "Wrong");

        firstDataSet.setColor(Color.rgb(30,141,30));
        secondDataSet.setColor(Color.rgb(28,226,20));
        thirdDataSet.setColor(Color.rgb(244,128,36));
        wrongDataSet.setColor(Color.rgb(233,31,31));

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(firstDataSet);
        dataSets.add(secondDataSet);
        dataSets.add(thirdDataSet);
        dataSets.add(wrongDataSet);
        BarData data = new BarData(dataSets);
        data.setBarWidth(0.9f); // set custom bar width
        data.setValueTextSize(10f);

        mChart.setData(data);

        Description desc = new Description();
        desc.setText("Attempts");
        mChart.setDescription(desc);
        mChart.setFitBars(true); // make the x-axis fit exactly all bars
        mChart.setBackgroundColor(Color.WHITE);
        mChart.invalidate(); // refresh
        mChart.animateXY(3000, 3000); // animate horizontal and vertical 3000 milliseconds
    }
}
