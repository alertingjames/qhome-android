package com.myapp.qhome.models;

import com.myapp.qhome.R;

public class Category {
    int idx = 0;
    String name = "";
    String logo_url = "";
    int logo_res = R.drawable.food;

    public Category(){}

    public void setLogo_res(int logo_res) {
        this.logo_res = logo_res;
    }

    public int getLogo_res() {
        return logo_res;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public int getIdx() {
        return idx;
    }

    public String getName() {
        return name;
    }

    public String getLogo_url() {
        return logo_url;
    }
}
