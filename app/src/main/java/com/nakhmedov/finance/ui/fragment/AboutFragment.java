package com.nakhmedov.finance.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.activity.MainActivity;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 5/4/17
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates
 */

public class AboutFragment extends BaseFragment {

    public static final String FRAG_TAG = "AboutFragment";

    @Override
    public int getLayoutId() {
        return R.layout.fragment_about;
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.about);

    }
}
