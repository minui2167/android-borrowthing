package com.minui.borrowthing.api;

import com.minui.borrowthing.model.ChatRoomRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatApi {
    // 채팅방 목록 가져오기
    @GET("/chat")
    Call<ChatRoomRes> getChatRoomList(@Header("Authorization") String accessToken);

    // 채팅방 생성
    @POST("/chat/{goodsId}")
    Call<ChatRoomRes> setChatRoom(@Header("Authorization") String accessToken, @Path("goodsId") int goodsId, @Query("type") String type);
}
