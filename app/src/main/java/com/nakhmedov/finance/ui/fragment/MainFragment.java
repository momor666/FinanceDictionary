package com.nakhmedov.finance.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.activity.CategoryActivity;
import com.nakhmedov.finance.ui.activity.MainActivity;
import com.nakhmedov.finance.ui.adapter.MainMenuAdapter;

import butterknife.BindView;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/22/17
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates
 */

public class MainFragment extends BaseFragment {

    @BindView(R.id.gridview) GridView gridView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);


        MainMenuAdapter menuAdapter = new MainMenuAdapter(getContext());
        gridView.setAdapter(menuAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        Intent categoryIntent = new Intent(getContext(), CategoryActivity.class);
                        categoryIntent.putExtra(CategoryActivity.EXTRA_VIEW_POSITION, CategoryActivity.CATEGORY_POSITION);
                        startActivity(categoryIntent);
                        break;
                    }
                    case 1: {
                        Intent quizIntent = new Intent(getContext(), CategoryActivity.class);
                        quizIntent.putExtra(CategoryActivity.EXTRA_VIEW_POSITION, CategoryActivity.QUIZ_POSITION);
                        startActivity(quizIntent);
                        break;
                    }
                    case 2: {
                        Toast.makeText(getContext(), getString(R.string.we_are_working_hard), Toast.LENGTH_LONG).show();
                        break;
                    }
                    case 3: {
                        ConverterFragment converterFragment = ConverterFragment.newInstance();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, converterFragment, ConverterFragment.FRAG_TAG)
                                .addToBackStack(null)
                                .commit();
                        break;
                    }
                    case 4: {
                        Intent starredIntent = new Intent(getContext(), CategoryActivity.class);
                        starredIntent.putExtra(CategoryActivity.EXTRA_VIEW_POSITION, CategoryActivity.STARRED_POSITION);
                        startActivity(starredIntent);
                        break;
                    }
                    case 5: {
                        AboutFragment aboutFragment = AboutFragment.newInstance();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, aboutFragment, AboutFragment.FRAG_TAG)
                                .addToBackStack(null)
                                .commit();
                        break;
                    }
                }
            }
        });


    }
}
