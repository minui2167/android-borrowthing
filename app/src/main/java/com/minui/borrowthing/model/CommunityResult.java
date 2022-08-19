package com.minui.borrowthing.model;

import java.util.List;

public class CommunityResult {
    private String result;
    private int count;
    private List<Community> items;

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

    public List<Community> getItems() {
        return items;
    }

    public void setItems(List<Community> items) {
        this.items = items;
    }
}
