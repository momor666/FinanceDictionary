package com.nakhmedov.finance.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/2/17
 * Time: 12:48 AM
 * To change this template use File | Settings | File Templates
 */

@Entity
public class Category {

    @Id
    private Long id;

    @NotNull
    private String name;

    private boolean starred;
    private long lastTermsUpdateTime;

    @Generated(hash = 204665961)
    public Category(Long id, @NotNull String name, boolean starred,
            long lastTermsUpdateTime) {
        this.id = id;
        this.name = name;
        this.starred = starred;
        this.lastTermsUpdateTime = lastTermsUpdateTime;
    }

    @Generated(hash = 1150634039)
    public Category() {
    }

    public Category(String name) {
        this.name = name;
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

    public boolean getStarred() {
        return this.starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public long getLastTermsUpdateTime() {
        return lastTermsUpdateTime;
    }

    public void setLastTermsUpdateTime(long lastTermsUpdateTime) {
        this.lastTermsUpdateTime = lastTermsUpdateTime;
    }
}
