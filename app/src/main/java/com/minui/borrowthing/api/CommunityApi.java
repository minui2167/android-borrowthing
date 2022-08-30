package com.minui.borrowthing.api;

import com.minui.borrowthing.model.Comment;
import com.minui.borrowthing.model.CommunityComment;
import com.minui.borrowthing.model.CommunityCommentResult;
import com.minui.borrowthing.model.CommunityResult;
import com.minui.borrowthing.model.UserRes;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @POST("/community/{postingId}/comment")
    Call<UserRes> setComment(@Header("Authorization") String accessToken, @Path("postingId") int postingId, @Body Comment comment);

    @GET("/community/{postingId}/comment")
    Call<CommunityCommentResult> getCommentList(@Path("postingId") int postingId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("/community/{postingId}/comment/login")
    Call<CommunityCommentResult> getCommentList(@Header("Authorization") String accessToken, @Path("postingId") int postingId, @Query("offset") int offset, @Query("limit") int limit);

    @PUT("/community/{postingId}/comment/{commentId}")
    Call<UserRes> reviseComment(@Header("Authorization") String accessToken, @Path("postingId") int postingId, @Path("commentId") int commentId, @Body Comment comment);

    @DELETE("/community/{postingId}/comment/{commentId}")
    Call<UserRes> deleteComment(@Header("Authorization") String accessToken, @Path("postingId") int postingId, @Path("commentId") int commentId);

    @Multipart
    @PUT("/community/{postingId}")
    Call<UserRes> reviseCommunity(@Header("Authorization") String accessToken, @Path("postingId") int postingId, @Part List<MultipartBody.Part> photo, @Part("content") RequestBody content);

    @DELETE("/community/{postingId}")
    Call<UserRes> deleteCommunity(@Header("Authorization") String accessToken, @Path("postingId") int postingId);

    @GET("/users/community")
    Call<CommunityResult> getMyCommunityList(@Query("offset") int offset, @Query("limit") int limit, @Header("Authorization") String accessToken);

    @GET("/users/likes")
    Call<CommunityResult> getMyLikesList(@Query("offset") int offset, @Query("limit") int limit, @Header("Authorization") String accessToken);
}
