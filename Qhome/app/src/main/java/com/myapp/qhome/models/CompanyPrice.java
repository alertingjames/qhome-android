package com.myapp.qhome.models;

public class CompanyPrice {
    int id = 0;
    double price = 0.0d;
    String description = "";

    public CompanyPrice(){}

    public void setId(int id) {
        this.id = id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
