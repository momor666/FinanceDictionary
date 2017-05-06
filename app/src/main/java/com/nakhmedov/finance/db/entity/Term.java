package com.nakhmedov.finance.db.entity;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.nakhmedov.finance.constants.ContextConstants;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import me.yokeyword.indexablerv.IndexableEntity;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/2/17
 * Time: 12:50 AM
 * To change this template use File | Settings | File Templates
 */

@Entity
public class Term extends RecentSearches implements IndexableEntity {

    @Id
    private Long id;

    @NotNull
    private String name;

    private boolean starred;

    @Nullable
    @SerializedName("desciption")
    private String description;

    @SerializedName("category_id")
    private Long categoryId;

    private transient int itemType = ContextConstants.TERM_TYPE;

    public Term(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Generated(hash = 142182234)
    public Term() {
    }

    @Generated(hash = 2115559111)
    public Term(Long id, @NotNull String name, boolean starred, String description,
            Long categoryId) {
        this.id = id;
        this.name = name;
        this.starred = starred;
        this.description = description;
        this.categoryId = categoryId;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFieldIndexBy() {
        return name;
    }

    @Override
    public void setFieldIndexBy(String indexByField) {
        this.name = indexByField;
    }

    @Override
    public void setFieldPinyinIndexBy(String pinyin) {
        this.name = pinyin;
    }
    public boolean getStarred() {
        return this.starred;
    }
    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public int getItemType() {
        return this.itemType;
    }

}
