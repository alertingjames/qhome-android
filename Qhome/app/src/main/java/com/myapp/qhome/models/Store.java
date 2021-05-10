package com.myapp.qhome.models;

import com.myapp.qhome.commons.Commons;

public class Store implements Comparable<Store>{
    int idx = 0;
    int userId = 0;
    String name = "";
    String logo_url = "";
    String category = "";
    String category2 = "";
    String description = "";
    String registered_time = "";
    String status = "";
    float ratings = 0.0f;
    int reviews = 0;
    int likes = 0;
    boolean liked = false;

    String ar_name = "";
    String ar_category = "";
    String ar_category2 = "";
    String ar_description = "";

    int priceId = 0;

    public Store(){

    }

    public void setPriceId(int priceId) {
        this.priceId = priceId;
    }

    public int getPriceId() {
        return priceId;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public void setAr_category2(String ar_category2) {
        this.ar_category2 = ar_category2;
    }

    public String getCategory2() {
        return category2;
    }

    public String getAr_category2() {
        return ar_category2;
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

    public String getAr_name() {
        return ar_name;
    }

    public String getAr_category() {
        return ar_category;
    }

    public String getAr_description() {
        return ar_description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikes() {
        return likes;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
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

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public int getIdx() {
        return idx;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getLogo_url() {
        return logo_url;
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

    public float getRatings() {
        return ratings;
    }

    public int getReviews() {
        return reviews;
    }

    @Override
    public int compareTo(Store other) {
        if(Commons.nameSort == 1 && Commons.lang.equals("en"))return this.name.compareToIgnoreCase(other.name);
        else if(Commons.nameSort == 2 && Commons.lang.equals("en"))return other.name.compareToIgnoreCase(this.name);
        else if(Commons.nameSort == 1 && Commons.lang.equals("ar"))return this.ar_name.compareToIgnoreCase(other.ar_name);
        else if(Commons.nameSort == 2 && Commons.lang.equals("ar"))return other.ar_name.compareToIgnoreCase(this.ar_name);
        else return 0;
    }
}

























