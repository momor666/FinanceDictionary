package com.nakhmedov.finance.db.entity;

import com.nakhmedov.finance.constants.ContextConstants;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/20/17
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates
 */

@Entity
public class RecentSearches {

    private String recentTermName;
    private transient int itemType = ContextConstants.RECENT_TYPE;

    @Generated(hash = 1502150677)
    public RecentSearches(String recentTermName) {
        this.recentTermName = recentTermName;
    }

    @Generated(hash = 1587001602)
    public RecentSearches() {
    }

    public int getItemType() {
        return this.itemType;
    }

    public String getRecentTermName() {
        return this.recentTermName;
    }

    public void setRecentTermName(String recentTermName) {
        this.recentTermName = recentTermName;
    }

}
