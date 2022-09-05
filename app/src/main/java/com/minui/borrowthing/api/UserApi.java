package com.minui.borrowthing.api;

import com.minui.borrowthing.model.Area;
import com.minui.borrowthing.model.AreaInfo;
import com.minui.borrowthing.model.AreaRes;
import com.minui.borrowthing.model.MyLocation;
import com.minui.borrowthing.model.User2;
import com.minui.borrowthing.model.UserRes;
import com.minui.borrowthing.model.User;
import com.minui.borrowthing.model.UsersLike;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserApi {
    @POST("/users/register")
    Call<UserRes> register(@Body User user);

    @POST("/users/login")
    Call<UserRes> login(@Body User user);

    @POST("/users/logout")
    Call<UserRes> logout(@Header("Authorization") String accessToken);

    @PUT("/users/edit")
    Call<UserRes> Revise(@Header("Authorization") String accessToken , @Body User2 user);

    @POST("/users/location")
    Call<UserRes> setLocation(@Header("Authorization") String accessToken, @Body MyLocation myLocation);

    @GET("/users/location")
    Call<AreaRes> getMyLocation(@Header("Authorization") String accessToken);

    @GET("/users/location/distance")
    Call<AreaRes> getActivityAreaList(@Header("Authorization") String accessToken);

    @PUT("/users/location/distance")
    Call<AreaRes> setActivityMeters(@Header("Authorization") String accessToken, @Body AreaInfo areaInfo);



}
