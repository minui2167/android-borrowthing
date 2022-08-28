package com.minui.borrowthing.model;

import java.io.Serializable;
import java.util.List;

public class ChatRoomRes implements Serializable {
    private  String result;
    private List<ChatRoom> items;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<ChatRoom> getItems() {
        return items;
    }

    public void setItems(List<ChatRoom> items) {
        this.items = items;
    }
}
