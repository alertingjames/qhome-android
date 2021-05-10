package com.myapp.qhome.models;

import com.google.android.gms.maps.model.LatLng;

public class Address {
    int id = 0;
    int userId = 0;
    String imei_id = "";
    String address = "";
    LatLng latLng = null;
    String area = "";
    String street = "";
    String house = "";
    String status = "";

    public Address(){

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setImei_id(String imei_id) {
        this.imei_id = imei_id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getImei_id() {
        return imei_id;
    }

    public String getAddress() {
        return address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getArea() {
        return area;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public String getStatus() {
        return status;
    }
}
