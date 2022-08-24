package com.minui.borrowthing.model;

import java.util.List;

public class BorrowCommentResult {
    private String result;
    private int count;
    private List<BorrowComment> items;

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

    public List<BorrowComment> getItems() {
        return items;
    }

    public void setItems(List<BorrowComment> items) {
        this.items = items;
    }
}
