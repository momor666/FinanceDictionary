package com.nakhmedov.finance.ui.listener;

import com.nakhmedov.finance.db.entity.Category;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/6/17
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates
 */

public interface OnItemClickListener<T> {
    void onItemClick(Category selectedCategory);
}
