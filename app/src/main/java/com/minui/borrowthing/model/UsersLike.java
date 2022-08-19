package com.minui.borrowthing.model;

import java.util.List;

public class UsersLike {
    private String result;
    private int count;
    private List<UsersLikeItem> items;

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

    public List<UsersLikeItem> getItems() {
        return items;
    }

    public void setItems(List<UsersLikeItem> items) {
        this.items = items;
    }
}
