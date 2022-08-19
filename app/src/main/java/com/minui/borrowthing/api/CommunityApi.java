package com.minui.borrowthing.api;

import com.minui.borrowthing.model.CommunityResult;
import com.minui.borrowthing.model.UserRes;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommunityApi {
    @GET("/community")
    Call<CommunityResult> getCommunityList(@Query("offset") int offset, @Query("limit") int limit);

    @GET("/community")
    Call<CommunityResult> getCommunityList(@Query("offset") int offset, @Query("limit") int limit, @Header("Authorization") String accessToken);

    @POST("/community/{postingId}/likes")
    Call<UserRes> setLike(@Path("postingId") int postingId, @Header("Authorization") String accessToken);

    @DELETE("/community/{postingId}/likes")
    Call<UserRes> setLikeCancel(@Path("postingId") int postingId, @Header("Authorization") String accessToken);
}
