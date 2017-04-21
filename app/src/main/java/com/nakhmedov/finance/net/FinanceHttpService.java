package com.nakhmedov.finance.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/8/17
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates
 */

public interface FinanceHttpService {

    @GET("category")
    Call<JsonArray> listCategory();

    @GET("definition")
    Call<JsonArray> listTerms();

    @GET("definition/category")
    Call<JsonArray> listTermsBy(@Query("category_id") long categoryId);

    @GET("definition/view")
    Call<JsonObject> getTermContent(@Query("id") long id);

    @GET("definition/search")
    Call<JsonArray> doSearch(@Query("name") String term);

}
