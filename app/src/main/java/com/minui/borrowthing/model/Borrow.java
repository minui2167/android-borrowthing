package com.minui.borrowthing.model;

public class Borrow {
    private String imgUrl;
    private String txtTitle;
    private String txtPrice;
    private String txtTag;
    private int traded;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTxtTitle() {
        return txtTitle;
    }

    public void setTxtTitle(String txtTitle) {
        this.txtTitle = txtTitle;
    }

    public String getTxtPrice() {
        return txtPrice;
    }

    public void setTxtPrice(String txtPrice) {
        this.txtPrice = txtPrice;
    }

    public String getTxtTag() {
        return txtTag;
    }

    public void setTxtTag(String txtTag) {
        this.txtTag = txtTag;
    }

    public int getTraded() {
        return traded;
    }

    public void setTraded(int traded) {
        this.traded = traded;
    }
}
