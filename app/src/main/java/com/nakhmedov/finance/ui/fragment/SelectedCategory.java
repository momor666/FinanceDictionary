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
 * Date: 4/6/17
 * Time: 12:02 PM
 * To change this template use File | Settings | File Templates
 */

public class SelectedCategory extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_selected_category, container, false);
    }
}
