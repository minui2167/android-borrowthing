package com.minui.borrowthing.model;

import java.util.Comparator;
import java.util.List;

public class Community {
    private int id;
    private int userId;
    private String content;
    private int viewCount;
    private String createdAt;
    private int imgCount;
    private int likesCount;
    private int commentCount;
    private List<imageUrl> imgUrl;
    private int isLike;

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<imageUrl> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<imageUrl> imgUrl) {
        this.imgUrl = imgUrl;
    }

    Comparator<Community> likesComparator = new Comparator<Community>() {
        @Override
        public int compare(Community community1, Community community2) {
            return community1.likesCount - community2.likesCount;
        }
    };

    Comparator<Community> createdAtComparator = new Comparator<Community>() {
        @Override
        public int compare(Community community1, Community community2) {
            return community1.createdAt.compareTo(community2.createdAt);
        }
    };
}
