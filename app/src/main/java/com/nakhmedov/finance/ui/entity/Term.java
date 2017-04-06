package com.nakhmedov.finance.ui.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/2/17
 * Time: 12:50 AM
 * To change this template use File | Settings | File Templates
 */

@Entity
public class Term {

    @Id
    private Long id;

    @NotNull
    private String name;
    @NotNull
    private String definition;
    @Generated(hash = 1648481445)
    public Term(Long id, @NotNull String name, @NotNull String definition) {
        this.id = id;
        this.name = name;
        this.definition = definition;
    }
    @Generated(hash = 142182234)
    public Term() {
    }

    public Term(String name, String definition) {
        this.name = name;
        this.definition = definition;
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
    public String getDefinition() {
        return this.definition;
    }
    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
