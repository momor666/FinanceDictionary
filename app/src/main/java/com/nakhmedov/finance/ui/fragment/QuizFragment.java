package com.nakhmedov.finance.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nakhmedov.finance.R;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/4/17
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates
 */

public class QuizFragment extends Fragment {

    private static QuizFragment quizFragment;

    public static QuizFragment getInstance() {
        if (quizFragment == null) {
            quizFragment = new QuizFragment();
        }

        return quizFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_quiz, container, false);
    }
}
