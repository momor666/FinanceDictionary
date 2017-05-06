package com.nakhmedov.finance.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.db.entity.Term;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.indexablerv.IndexableAdapter;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/11/17
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates
 */

public class TermsAdapter extends IndexableAdapter<Term> {

    private final LayoutInflater mInflater;

    public TermsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_index_term, parent, false);
        return new IndexVH(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_term, parent, false);
        return new ContentVH(view);
    }

    @Override
    public void onBindTitleViewHolder(RecyclerView.ViewHolder viewHolder, String indexTitle) {
        IndexVH vh = (IndexVH) viewHolder;
        vh.termIndex.setText(indexTitle);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder viewHolder, Term term) {
        ContentVH vh = (ContentVH) viewHolder;
        String name = term.getName();
        vh.termName.setText(name);
        vh.termLogoName.setText(String.valueOf(Character.toUpperCase(name.charAt(0))));
    }

    class IndexVH extends RecyclerView.ViewHolder {

        @BindView(R.id.term_index) TextView termIndex;

        public IndexVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ContentVH extends RecyclerView.ViewHolder {

        @BindView(R.id.term_name) TextView termName;
        @BindView(R.id.item_logo) TextView termLogoName;

        public ContentVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
