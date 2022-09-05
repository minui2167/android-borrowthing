package com.minui.borrowthing.model;

public class AreaInfo {
//    "id": 8,
//    "siggAreaId": 1,
//    "name": "청라동",
//    "latitude": 37.5322829,
//    "longitude": 126.6537116,
//    "sidoAreaId": 1
//          "userId": 1,
//        "emdId": 1,
//        "activityMeters": 5000,
//        "sido": "인천광역시",
//        "sigg": "서구",
//        "emd": "검암동"
    private int id;
    private int siggAreaId;
    private String name;
    private double latitude;
    private double longitude;
    private int sidoAreaId;
    private int userId;
    private int emdId;
    private int activityMeters;
    private String sido;
    private String sigg;
    private String emd;

    public AreaInfo(int activityMeters) {
        this.activityMeters = activityMeters;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSiggAreaId() {
        return siggAreaId;
    }

    public void setSiggAreaId(int siggAreaId) {
        this.siggAreaId = siggAreaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getSidoAreaId() {
        return sidoAreaId;
    }

    public void setSidoAreaId(int sidoAreaId) {
        this.sidoAreaId = sidoAreaId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEmdId() {
        return emdId;
    }

    public void setEmdId(int emdId) {
        this.emdId = emdId;
    }

    public int getActivityMeters() {
        return activityMeters;
    }

    public void setActivityMeters(int activityMeters) {
        this.activityMeters = activityMeters;
    }

    public String getSido() {
        return sido;
    }

    public void setSido(String sido) {
        this.sido = sido;
    }

    public String getSigg() {
        return sigg;
    }

    public void setSigg(String sigg) {
        this.sigg = sigg;
    }

    public String getEmd() {
        return emd;
    }

    public void setEmd(String emd) {
        this.emd = emd;
    }
}
