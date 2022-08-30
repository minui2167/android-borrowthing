package com.minui.borrowthing.api;

import com.minui.borrowthing.model.BorrowCommentResult;
import com.minui.borrowthing.model.BorrowResult;
import com.minui.borrowthing.model.Comment;
import com.minui.borrowthing.model.Score;
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

public interface BorrowApi {
    @GET("/goods")
    Call<BorrowResult> getGoods(@Query("offset") int offset, @Query("limit") int limit, @Query("category") int category);

    @GET("/goods/login")
    Call<BorrowResult> getGoods(@Query("offset") int offset, @Query("limit") int limit, @Query("category") int category, @Header("Authorization") String accessToken);

    @POST("/goods/{goodsId}/wish")
    Call<UserRes> setConcerned(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId);

    @DELETE("/goods/{goodsId}/wish")
    Call<UserRes> setConcernedCancel(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId);

    @GET("/goods/{goodsId}/comment")
    Call<BorrowCommentResult> getCommentList(@Path("goodsId") int goodsId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("/goods/{goodsId}/comment/login")
    Call<BorrowCommentResult> getCommentList(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId, @Query("offset") int offset, @Query("limit") int limit);

    @POST("/goods/{goodsId}/comment")
    Call<UserRes> setComment(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId, @Body Comment comment);

    @PUT("/goods/{goodsId}/comment/{commentId}")
    Call<UserRes> reviseComment(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId, @Path("commentId") int commentId, @Body Comment comment);

    @DELETE("/goods/{goodsId}/comment/{commentId}")
    Call<UserRes> deleteComment(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId, @Path("commentId") int commentId);

    @Multipart
    @POST("/goods")
    Call<UserRes> setBorrow(@Header("Authorization") String accessToken, @Part("title") RequestBody title, @Part List<MultipartBody.Part> photo, @Part("content") RequestBody content, @Part("price") RequestBody price, @Part("rentalPeriod") RequestBody rentalPeriod, @Part("categoriId") RequestBody categoriId);

    @Multipart
    @PUT("/goods/{goodsId}")
    Call<UserRes> reviseBorrow(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId, @Part("title") RequestBody title, @Part List<MultipartBody.Part> photo, @Part("content") RequestBody content, @Part("price") RequestBody price, @Part("rentalPeriod") RequestBody rentalPeriod, @Part("categoriId") RequestBody categoriId);

    @DELETE("/goods/{goodsId}")
    Call<UserRes> deleteBorrow(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId);

    @GET("/goods/recommend")
    Call<BorrowResult> getRecommend(@Header("Authorization") String accessToken, @Query("offset") int offset, @Query("limit") int limit);

    @GET("/users/sale")
    Call<BorrowResult> getSalesHistory(@Header("Authorization") String accessToken, @Query("offset") int offset, @Query("limit") int limit, @Query("status") int status);

    @GET("/users/buy")
    Call<BorrowResult> getPurchaseHistory(@Header("Authorization") String accessToken, @Query("offset") int offset, @Query("limit") int limit, @Query("status") int status);

    @GET("/users/buy/notrating")
    Call<BorrowResult> getNotRatingPurchaseHistory(@Header("Authorization") String accessToken, @Query("offset") int offset, @Query("limit") int limit);

    @POST("/evaluation/{goodsId}")
    Call<BorrowResult> setRating(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId, @Body Score score);

    @PUT("/goods/{goodsId}/deal")
    Call<BorrowResult> setTransactionCompletion(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId);

    @POST("/goods/{goodsId}/deal")
    Call<BorrowResult> setTransactionRequest(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId);

    @GET("/users/wishlist")
    Call<BorrowResult> getWishList(@Header("Authorization") String accessToken, @Query("offset") int offset, @Query("limit") int limit);

    @GET("/goods/area")
    Call<BorrowResult> getGoodsInActivityArea(@Header("Authorization") String accessToken, @Query("offset") int offset, @Query("limit") int limit, @Query("sidoId") int sidoId, @Query("siggId") int siggId, @Query("emdId") int emdId);

    @GET("/goods/search")
    Call<BorrowResult> getSearchedGoods(@Query("offset") int offset, @Query("limit") int limit, @Query("keyword") String keyword, @Query("category") int category);

    @GET("/goods/search/login")
    Call<BorrowResult> getSearchedGoods(@Header("Authorization") String accessToken, @Query("offset") int offset, @Query("limit") int limit, @Query("keyword") String keyword, @Query("category") int category);

}
