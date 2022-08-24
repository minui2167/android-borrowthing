package com.minui.borrowthing.api;

import com.minui.borrowthing.model.BorrowResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface BorrowApi {
    @GET("/goods")
    Call<BorrowResult> getGoods(@Query("offset") int offset, @Query("limit") int limit);

    @GET("/goods")
    Call<BorrowResult> getGoods(@Query("offset") int offset, @Query("limit") int limit, @Header("Authorization") String accessToken);
}
