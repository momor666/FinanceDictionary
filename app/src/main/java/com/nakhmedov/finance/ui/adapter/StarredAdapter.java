package com.nakhmedov.finance.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.entity.Term;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/6/17
 * Time: 10:58 PM
 * To change this template use File | Settings | File Templates
 */

public class StarredAdapter extends RecyclerView.Adapter<StarredAdapter.ViewHolder> {

    private final List<Term> termListList;
    private Context mContext;

    public StarredAdapter(Context context, List<Term> termListList) {
        this.mContext = context;
        this.termListList = termListList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.category_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.starredTextView.setText(termListList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return termListList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view) TextView starredTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
