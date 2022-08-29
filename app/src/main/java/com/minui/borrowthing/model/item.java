package com.minui.borrowthing.model;

import java.io.Serializable;
import java.util.List;

public class item implements Serializable {
    private int id;
    private int categoriId;
    private int sellerId;
    private String title;
    private String content;
    private int price;
    private int viewCount;
    private String rentalPeriod;
    private int status;
    private String createdAt;
    private String updatedAt;
    private String nickname;
    private int wishCount;
    private int commentCount;
    private int imgCount;
    private List<imageUrl> imgUrl;
    private List<tag> tag;
    private int isWish;
    private int isAuthor;
    private String emdName;
    private double latitude;
    private double longitude;
    private int emdId;
    private int siggAreaId;
    private int sidoAreaId;

    public int getIsAuthor() {
        return isAuthor;
    }

    public void setIsAuthor(int isAuthor) {
        this.isAuthor = isAuthor;
    }

    public int getIsWish() {
        return isWish;
    }

    public void setIsWish(int isWish) {
        this.isWish = isWish;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoriId() {
        return categoriId;
    }

    public void setCategoriId(int categoriId) {
        this.categoriId = categoriId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(String rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getWishCount() {
        return wishCount;
    }

    public void setWishCount(int wishCount) {
        this.wishCount = wishCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public List<imageUrl> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<imageUrl> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<com.minui.borrowthing.model.tag> getTag() {
        return tag;
    }

    public void setTag(List<com.minui.borrowthing.model.tag> tag) {
        this.tag = tag;
    }

    public String getEmdName() {
        return emdName;
    }

    public void setEmdName(String emdName) {
        this.emdName = emdName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getEmdId() {
        return emdId;
    }

    public void setEmdId(int emdId) {
        this.emdId = emdId;
    }

    public int getSiggAreaId() {
        return siggAreaId;
    }

    public void setSiggAreaId(int siggAreaId) {
        this.siggAreaId = siggAreaId;
    }

    public int getSidoAreaId() {
        return sidoAreaId;
    }

    public void setSidoAreaId(int sidoAreaId) {
        this.sidoAreaId = sidoAreaId;
    }
}
