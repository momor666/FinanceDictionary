package com.nakhmedov.finance.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.db.entity.DaoSession;
import com.nakhmedov.finance.db.entity.Term;
import com.nakhmedov.finance.db.entity.TermDao;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.activity.BaseActivity;

import java.util.List;

import butterknife.BindView;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/12/17
 * Time: 10:47 PM
 * To change this template use File | Settings | File Templates
 */

public class ContentTermsFragment extends BaseFragment {

    public static final String TAG_FRAG = "ContentTermsFragment";
    public @BindView(R.id.term_viewpager) ViewPager mViewPager;

    private static final String KEY_TERM_ID = "selected_term_id";
    private static final String KEY_CATEGORY_ID = "selected_category_id";

    private int currentPosition = -1;
    private DaoSession daoSession;

    OnTermsPositionChangeListener mCallBack;
    private SparseArray<ViewTermContent> mPageReferenceMap = new SparseArray<>(10);
    private int previousPosition = 0;

    public static Fragment newInstance(Long termId, Long categoryId) {
        ContentTermsFragment termsFragment = new ContentTermsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_TERM_ID, termId);
        bundle.putLong(KEY_CATEGORY_ID, categoryId);
        termsFragment.setArguments(bundle);

        return termsFragment;
    }

    public interface OnTermsPositionChangeListener {
        void onTermPositionChanged(int position);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_term_content;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCallBack = (OnTermsPositionChangeListener) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        daoSession = FinanceApp.getApplication(getContext())
                .getDaoSession();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            long termId = bundle.getLong(KEY_TERM_ID);
            long categoryId = bundle.getLong(KEY_CATEGORY_ID);
            updateOrRenderUi(termId, categoryId);
        }
    }

    public void updateOrRenderUi(long termId, long categoryId) {
        List<Term> termList = daoSession
                .getTermDao()
                .queryBuilder()
                .where(TermDao.Properties.CategoryId.eq(categoryId))
                .build()
                .list();

        for (int i = 0; i < termList.size(); i++) {
            Term term = termList.get(i);
            if (term.getId().compareTo(termId) == 0) {
                currentPosition = i;
                break;
            }
        }

        CustomAdapter customAdapter = new CustomAdapter(getChildFragmentManager(), termList);
        mViewPager.setAdapter(customAdapter);

        mViewPager.setCurrentItem(currentPosition);
        previousPosition = currentPosition;
        //improvements to load terms early
        mViewPager.setOffscreenPageLimit(5);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCallBack.onTermPositionChanged(previousPosition);
                previousPosition = position;
                ((BaseActivity) getActivity()).requestNewInterstitial();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class CustomAdapter extends FragmentStatePagerAdapter {
        private final List<Term> termList;

        CustomAdapter(FragmentManager fm, List<Term> termList) {
            super(fm);
            this.termList = termList;
        }

        @Override
        public Fragment getItem(int position) {
            Term term = termList.get(position);
            ViewTermContent viewTermFrag = ViewTermContent
                    .newInstance(term.getId(), term.getCategoryId());
            mPageReferenceMap.put(position, viewTermFrag);

            return viewTermFrag;

        }

        @Override
        public int getCount() {
            return termList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mPageReferenceMap.remove(position);
            super.destroyItem(container, position, object);
        }

        public ViewTermContent getFragment(int key) {
            return mPageReferenceMap.get(key);
        }
    };
}
