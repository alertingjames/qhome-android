package com.myapp.qhome.models;

import com.myapp.qhome.commons.Commons;

import java.util.ArrayList;

public class Product{
    int idx = 0;
    int storeId = 0;
    int userId = 0;
    String name = "";
    String picture_url = "";
    String category = "";
    double price = 0.0f;
    double new_price = 0.0f;
    String unit = "";
    String description = "";
    String registered_time = "";
    String status = "";
    int likes = 0;
    boolean liked = false;

    double comPrice = 0.0d;

    String ar_name = "";
    String ar_category = "";
    String ar_description = "";

    String store_name = "";
    String ar_store_name = "";

    ArrayList<String> _pictureList = new ArrayList<>();

    public Product(){

    }

    public void setComPrice(double comPrice) {
        this.comPrice = comPrice;
    }

    public double getComPrice() {
        return comPrice;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public void setAr_store_name(String ar_store_name) {
        this.ar_store_name = ar_store_name;
    }

    public String getStore_name() {
        return store_name;
    }

    public String getAr_store_name() {
        return ar_store_name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setNew_price(double new_price) {
        this.new_price = new_price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public double getNew_price() {
        return new_price;
    }

    public String getUnit() {
        return unit;
    }

    public void set_pictureList(ArrayList<String> _pictureList) {
        this._pictureList.clear();
        this._pictureList.addAll(_pictureList);
    }

    public ArrayList<String> get_pictureList() {
        return _pictureList;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRegistered_time(String registered_time) {
        this.registered_time = registered_time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setAr_name(String ar_name) {
        this.ar_name = ar_name;
    }

    public void setAr_category(String ar_category) {
        this.ar_category = ar_category;
    }

    public void setAr_description(String ar_description) {
        this.ar_description = ar_description;
    }

    public int getIdx() {
        return idx;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getRegistered_time() {
        return registered_time;
    }

    public String getStatus() {
        return status;
    }

    public int getLikes() {
        return likes;
    }

    public boolean isLiked() {
        return liked;
    }

    public String getAr_name() {
        return ar_name;
    }

    public String getAr_category() {
        return ar_category;
    }

    public String getAr_description() {
        return ar_description;
    }
}























