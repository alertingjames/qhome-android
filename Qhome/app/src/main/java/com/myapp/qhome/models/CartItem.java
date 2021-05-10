package com.myapp.qhome.models;

public class CartItem {
    int id = 1;
    String imei_id = "";
    int producer_id = 1;
    int store_id = 1;
    String store_name = "";
    String ar_store_name = "";
    int product_id = 1;
    String product_name = "";
    String ar_product_name = "";
    String category = "";
    String ar_category = "";
    double price = 0.0f;
    String unit = "qr";
    int quantity = 0;
    String date_time = "";
    String picture_url = "";
    String status = "";
    int price_id = 0;

    public CartItem(){

    }

    public void setPrice_id(int price_id) {
        this.price_id = price_id;
    }

    public int getPrice_id() {
        return price_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setImei_id(String imei_id) {
        this.imei_id = imei_id;
    }

    public String getImei_id() {
        return imei_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProducer_id(int producer_id) {
        this.producer_id = producer_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public void setAr_store_name(String ar_store_name) {
        this.ar_store_name = ar_store_name;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setAr_product_name(String ar_product_name) {
        this.ar_product_name = ar_product_name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAr_category(String ar_category) {
        this.ar_category = ar_category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public int getId() {
        return id;
    }

    public int getProducer_id() {
        return producer_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public String getAr_store_name() {
        return ar_store_name;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getAr_product_name() {
        return ar_product_name;
    }

    public String getCategory() {
        return category;
    }

    public String getAr_category() {
        return ar_category;
    }

    public double getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getPicture_url() {
        return picture_url;
    }
}
