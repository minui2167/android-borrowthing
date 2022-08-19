package com.minui.borrowthing.api;

import com.minui.borrowthing.model.CommunityResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CommunityApi {
    @GET("/community")
    Call<CommunityResult> getCommunityList(@Query("offset") int offset, @Query("limit") int limit);
}
