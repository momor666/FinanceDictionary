package com.nakhmedov.finance.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.ui.entity.Category;
import com.nakhmedov.finance.ui.listener.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/2/17
 * Time: 10:58 PM
 * To change this template use File | Settings | File Templates
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements
        OnItemClickListener {

    private final List<Category> categoryList;
    private Context mContext;
    private OnItemClickListener mListener;

    public CategoryAdapter(Context context, List<Category> categoryList, OnItemClickListener listener) {
        this.mContext = context;
        this.categoryList = categoryList;
        this.mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.category_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.categoryText.setText(categoryList.get(position).getName());

        holder.categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onItemClick(int position) {
        mListener.onItemClick(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view) TextView categoryText;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
