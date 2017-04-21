package com.nakhmedov.finance.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.entity.DaoSession;
import com.nakhmedov.finance.ui.entity.Term;
import com.nakhmedov.finance.ui.fragment.StarredFragment;

import java.util.ArrayList;
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

public class StarredAdapter extends RecyclerView.Adapter<StarredAdapter.ViewHolder>
                implements StarredFragment.OnStarredItemClickListener {

    private final StarredFragment.OnStarredItemClickListener mListener;
    private List<Term> termListList = new ArrayList<>();
    private Context mContext;

    public StarredAdapter(Context context, StarredFragment.OnStarredItemClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.starred_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final int selectedPosition = holder.getAdapterPosition();
        final Term selectedTerm = termListList.get(selectedPosition);

        holder.starredTextView.setText(selectedTerm.getName());
        holder.likeButton.setLiked(true);

        holder.starredTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(selectedTerm.getId(), selectedTerm.getCategoryId());
            }
        });

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                selectedTerm.setStarred(false);
                DaoSession daoSession = ((FinanceApp) mContext.getApplicationContext()).getDaoSession();
                daoSession.getTermDao()
                        .update(selectedTerm);

                termListList.remove(selectedPosition);
                notifyItemRemoved(selectedPosition);

            }
        });
    }

    @Override
    public int getItemCount() {
        return termListList.size();
    }

    public void setData(List<Term> termList) {
        this.termListList = termList;
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(long termId, long categoryId) {
        mListener.onItemClick(termId, categoryId);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view) TextView starredTextView;
        @BindView(R.id.like_red_btn) LikeButton likeButton;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
