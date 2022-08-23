package com.minui.borrowthing.model;

import java.util.List;

public class CommunityCommentResult {
    private String result;
    private int count;
    private List<CommunityComment> items;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CommunityComment> getItems() {
        return items;
    }

    public void setItems(List<CommunityComment> items) {
        this.items = items;
    }
}
