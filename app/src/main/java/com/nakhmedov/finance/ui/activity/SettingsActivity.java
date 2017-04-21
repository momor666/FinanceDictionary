package com.nakhmedov.finance.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.fragment.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

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
}
