package com.minui.borrowthing.model;

import java.io.Serializable;

public class ChatRoom implements Serializable {
    int id;
    int goodsId;
    int buyerId;
    String createdAt;
    String title;
    int sellerId;
    String buyerNickname;
    String sellerNickname;
    int myId;
    String updatedAt;

    public ChatRoom() {
    }

    public ChatRoom(int id, int goodsId, int buyerId, String createdAt, String title, int sellerId, String updatedAt) {
        this.id = id;
        this.goodsId = goodsId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
        this.title = title;
        this.sellerId = sellerId;
        this.updatedAt = updatedAt;
    }

    public ChatRoom(int id, int goodsId, int buyerId, String createdAt, int myId) {
        this.id = id;
        this.goodsId = goodsId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
        this.myId = myId;
    }

    public ChatRoom(int id, int goodsId, int buyerId, String createdAt, String buyerNickname, int myId) {
        this.id = id;
        this.goodsId = goodsId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
        this.buyerNickname = buyerNickname;
        this.myId = myId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getBuyerNickname() {
        return buyerNickname;
    }

    public void setBuyerNickname(String buyerNickname) {
        this.buyerNickname = buyerNickname;
    }

    public String getSellerNickname() {
        return sellerNickname;
    }

    public void setSellerNickname(String sellerNickname) {
        this.sellerNickname = sellerNickname;
    }

    public void setMyId(int myId) {
        this.myId = myId;
    }

    public int getMyId() {
        return myId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
