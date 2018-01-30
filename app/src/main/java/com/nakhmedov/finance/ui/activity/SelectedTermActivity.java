package com.nakhmedov.finance.ui.activity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.ui.fragment.ContentTermsFragment;
import com.nakhmedov.finance.ui.fragment.ViewTermContent;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/17/17
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates
 */

public class SelectedTermActivity extends BaseActivity implements ContentTermsFragment.OnTermsPositionChangeListener {
    public static final String EXTRA_TERM_ID = "extra_term_id";
    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    public static final String EXTRA_DAILY_ALARM = "extra_daily_alarm";

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_selected_term;
    }

//    @Override
//    public boolean needToolbar() {
//        return false;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showBackBtn();

        long extraTermId = 0, extraCategoryId = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            extraTermId = extras.getLong(EXTRA_TERM_ID);
            extraCategoryId = extras.getLong(EXTRA_CATEGORY_ID);
            boolean isDailyAlarm = extras.getBoolean(EXTRA_DAILY_ALARM);
            if (isDailyAlarm) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotificationManager.cancel(ContextConstants.NTFY_DAILY_ID);
            }
        }
        if (savedInstanceState == null) {
            ContentTermsFragment selectedTermFragment = (ContentTermsFragment) ContentTermsFragment.newInstance(extraTermId, extraCategoryId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, selectedTermFragment, ContentTermsFragment.TAG_FRAG).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onTermPositionChanged(int position) {
        ContentTermsFragment contentTermsFragment = (ContentTermsFragment)
                getSupportFragmentManager().findFragmentByTag(ContentTermsFragment.TAG_FRAG);
        ViewTermContent viewTermContent = ((ContentTermsFragment.CustomAdapter)
                contentTermsFragment.mViewPager.getAdapter()).getFragment(position);
        if (viewTermContent != null) {
            viewTermContent.pauseSpeech(false);
        }
    }
}
