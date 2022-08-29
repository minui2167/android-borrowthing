package com.minui.borrowthing.model;

public class AreaInfo {
//    "id": 8,
//    "siggAreaId": 1,
//    "name": "청라동",
//    "latitude": 37.5322829,
//    "longitude": 126.6537116,
//    "sidoAreaId": 1

    int id;
    int siggAreaId;
    String name;
    double latitude;
    double longitude;
    int sidoAreaId;

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
}
