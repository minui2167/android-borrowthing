package com.minui.borrowthing.model;

import java.io.Serializable;
import java.util.List;

public class AreaRes implements Serializable{
    String result;
    int count;
    List<AreaInfo> items;

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

    public List<AreaInfo> getItems() {
        return items;
    }

    public void setItems(List<AreaInfo> items) {
        this.items = items;
    }
}
