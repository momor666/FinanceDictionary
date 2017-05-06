package com.nakhmedov.finance.db.entity;

import org.greenrobot.greendao.annotation.Entity;

import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 5/5/17
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates
 */

@Entity
public class Quiz {
    private String qustionText;
    private int attempts;
    private String answer;

    public Quiz(String questionText, String answer) {
        this.qustionText = questionText;
        this.answer = answer;
    }

    @Generated(hash = 1888746628)
    public Quiz(String qustionText, int attempts, String answer) {
        this.qustionText = qustionText;
        this.attempts = attempts;
        this.answer = answer;
    }

    @Generated(hash = 1436513302)
    public Quiz() {
    }

    public String getQustionText() {
        return qustionText;
    }

    public void setQustionText(String qustionText) {
        this.qustionText = qustionText;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
