package com.nakhmedov.finance.db.entity;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/22/17
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates
 */

public class Country {
    private String name;
    private String currency;

    public Country(String countryName, String currencyName) {
        this.name = countryName;
        this.currency = currencyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return name + " (" + currency + ")";
    }
}
