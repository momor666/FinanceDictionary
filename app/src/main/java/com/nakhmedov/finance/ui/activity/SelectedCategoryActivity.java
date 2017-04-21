package com.nakhmedov.finance.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.fragment.SelectedCategory;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/7/17
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates
 */

public class SelectedCategoryActivity extends BaseActivity implements SelectedCategory.OnTermSelectedListener {

    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_CATEGORY_ID = "extra_id";

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_selected_category;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            SelectedCategory selectedCategoryFragment = SelectedCategory.newInstance(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, selectedCategoryFragment, SelectedCategory.FRAG_TAG).commit();
        }
    }

    @Override
    public void onTermSelected(long termId, long categoryId) {
        Intent intent = new Intent(SelectedCategoryActivity.this, SelectedTermActivity.class);
        intent.putExtra(SelectedTermActivity.EXTRA_TERM_ID, termId);
        intent.putExtra(SelectedTermActivity.EXTRA_CATEGORY_ID, categoryId);
        startActivity(intent);
    }
}
