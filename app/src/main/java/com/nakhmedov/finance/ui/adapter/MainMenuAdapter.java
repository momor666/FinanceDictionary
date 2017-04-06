package com.nakhmedov.finance.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nakhmedov.finance.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 3/30/17
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates
 */

public class MainMenuAdapter extends BaseAdapter {

    @BindString(R.string.categories) String categories;
    @BindString(R.string.quiz) String quiz;
    @BindString(R.string.stockWatch) String stockWatch;
    @BindString(R.string.loanCalc) String loanCalc;

    private final Context mContext;
//    private String menus[] = {categories, quiz, stockWatch, loanCalc, stockWatch, loanCalc};
    private String menus[] = {"Categories", "Quiz", "Loan Calculator", "Converter", "Starred", "About"};
    private int images[] = {R.drawable.main_icon_1, R.drawable.main_icon_2, R.drawable.main_icon_3,
            R.drawable.main_icon_4, R.drawable.main_icon_1,R.drawable.main_icon_2};

    public MainMenuAdapter(Context context) {
        this.mContext = context;
//        new ViewBinder_MainMenuAdapter(MainMenuAdapter.this, context);
    }

    @Override
    public int getCount() {
        return menus.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.main_menu_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.name.setText(menus[position]);
        holder.imageView.setImageResource(images[position]);

        return view;

    }

    static class ViewHolder {
        @BindView(R.id.image) ImageView imageView;
        @BindView(R.id.title) TextView name;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}