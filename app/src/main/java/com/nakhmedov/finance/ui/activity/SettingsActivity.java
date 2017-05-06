package com.nakhmedov.finance.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.fragment.SettingsFragment;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/6/17
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates
 */

public class SettingsActivity extends BaseActivity {

    @Override
    public int getLayoutResourceId() {
        return R.layout.settings;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showBackBtn();

        if (savedInstanceState == null) {
            SettingsFragment settingsFragment = new SettingsFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, settingsFragment)
                    .commit();
        }
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
}
