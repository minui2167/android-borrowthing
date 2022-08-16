package com.minui.borrowthing.config;

import com.minui.borrowthing.model.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface LocationApi {

    @GET("/map-reversegeocode/v2/gc")
    Call<Result> getLocation(@Query("coords") String coords,
                             @Query("orders") String orders,
                             @Query("output") String output,
                             @Header("X-NCP-APIGW-API-KEY-ID") String id,
                             @Header("X-NCP-APIGW-API-KEY") String password);
}
