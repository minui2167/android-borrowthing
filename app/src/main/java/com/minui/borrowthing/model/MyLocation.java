package com.minui.borrowthing.model;

public class MyLocation {
    private String sidoName;
    private String siggName;
    private String emdName;

    public MyLocation(String sidoName, String siggName, String emdName) {
        this.sidoName = sidoName;
        this.siggName = siggName;
        this.emdName = emdName;
    }

    public String getSidoName() {
        return sidoName;
    }

    public void setSidoName(String sidoName) {
        this.sidoName = sidoName;
    }

    public String getSiggName() {
        return siggName;
    }

    public void setSiggName(String siggName) {
        this.siggName = siggName;
    }

    public String getEmdName() {
        return emdName;
    }

    public void setEmdName(String emdName) {
        this.emdName = emdName;
    }
}
