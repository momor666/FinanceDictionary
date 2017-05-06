package com.nakhmedov.finance.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.db.entity.RecentSearches;
import com.nakhmedov.finance.db.entity.Term;
import com.nakhmedov.finance.ui.activity.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/20/17
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final SearchActivity.OnSearchSubmitListener mListener;
    private List<RecentSearches> historyList = new ArrayList<>(10);

    public SearchAdapter(Context context, SearchActivity.OnSearchSubmitListener listener) {
        this.mContext = context;
        this.mListener = listener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ContextConstants.TERM_TYPE: {
                View itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_term, parent, false);
                return new TermViewHolder(itemView);
            }
            case ContextConstants.RECENT_TYPE: {
                View itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.search_item, parent, false);
                return new RecentViewHolder(itemView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int selectedPosition = holder.getAdapterPosition();
        switch (holder.getItemViewType()) {
            case ContextConstants.TERM_TYPE: {
                final Term term = (Term) historyList.get(selectedPosition);
                TermViewHolder viewHolder = (TermViewHolder) holder;
                viewHolder.termNameView.setText(term.getName());
                viewHolder.termNameView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onSelectedTerm(term);
                    }
                });
                break;
            }
            case ContextConstants.RECENT_TYPE: {
                final String recentName = historyList.get(selectedPosition).getRecentTermName();
                RecentViewHolder recentHolder = (RecentViewHolder) holder;
                recentHolder.recentTitle.setText(recentName);
                recentHolder.recentTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onSubmit(recentName);
                    }
                });

                recentHolder.searchCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onSetText(recentName);
                    }
                });

                break;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        RecentSearches currentTerm = historyList.get(position);
        if (currentTerm.getItemType() == ContextConstants.RECENT_TYPE) {
            return ContextConstants.RECENT_TYPE;
        }
        return ContextConstants.TERM_TYPE;
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void swapData(List<RecentSearches> histories) {
        this.historyList = histories;
        notifyDataSetChanged();
    }

    class RecentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) TextView recentTitle;
        @BindView(R.id.search_commit) ImageView searchCommit;

        RecentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TermViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.term_name) TextView termNameView;

        TermViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
