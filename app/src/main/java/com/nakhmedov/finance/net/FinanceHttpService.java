package com.nakhmedov.finance.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
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

    @GET("converter")
    Call<ResponseBody> getConvertRate(@Query("a") String amount, @Query("from") String firstCurrency, @Query("to") String secondCurrency);

}
