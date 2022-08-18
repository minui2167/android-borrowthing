package com.minui.borrowthing.model;

import java.util.List;

public class BorrowResult {
    private String result;
    private int count;
    private List<item> items;
    private List<itemImage> itemImages;

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

}
