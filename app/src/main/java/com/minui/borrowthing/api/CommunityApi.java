package com.minui.borrowthing.api;

import com.minui.borrowthing.model.CommunityResult;
import com.minui.borrowthing.model.UserRes;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommunityApi {
    @GET("/community")
    Call<CommunityResult> getCommunityList(@Query("offset") int offset, @Query("limit") int limit);

    @GET("/community/login")
    Call<CommunityResult> getCommunityList(@Query("offset") int offset, @Query("limit") int limit, @Header("Authorization") String accessToken);

    @POST("/community/{postingId}/likes")
    Call<UserRes> setLike(@Path("postingId") int postingId, @Header("Authorization") String accessToken);

    @DELETE("/community/{postingId}/likes")
    Call<UserRes> setLikeCancel(@Path("postingId") int postingId, @Header("Authorization") String accessToken);

    @Multipart
    @POST("/community")
    Call<UserRes> setCommunity(@Header("Authorization") String accessToken, @Part List<MultipartBody.Part> photo, @Part("content") RequestBody content);
}
