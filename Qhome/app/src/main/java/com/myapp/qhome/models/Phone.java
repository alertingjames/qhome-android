package com.myapp.qhome.models;

public class Phone {
    int id = 0;
    int userId = 0;
    String imei_id = "";
    String phone_number = "";
    String status = "";

    public Phone(){

    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
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

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
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

    public String getPhone_number() {
        return phone_number;
    }
}
