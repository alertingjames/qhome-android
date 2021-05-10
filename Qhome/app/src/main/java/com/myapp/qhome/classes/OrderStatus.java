package com.myapp.qhome.classes;

import android.app.Activity;
import android.content.Context;

import com.myapp.qhome.R;

import java.util.HashMap;
import java.util.Map;

public class OrderStatus {
    public Map<String,String> statusStr = new HashMap<>();
    public Map<String,String> nextStatus = new HashMap<>();
    public Map<String,String> nextStatusStr = new HashMap<>();
    public Map<String,Integer> statusIndex = new HashMap<>();

    public void initOrderStatus(Activity cxt){
        statusStr.put("placed", cxt.getString(R.string.placed));
        statusStr.put("confirmed", cxt.getString(R.string.confirmed));
        statusStr.put("prepared", cxt.getString(R.string.prepared));
        statusStr.put("ready", cxt.getString(R.string.ready));
        statusStr.put("delivered", cxt.getString(R.string.delivered));

        nextStatus.put("placed", "confirmed");
        nextStatus.put("confirmed", "prepared");
        nextStatus.put("prepared", "ready");
        nextStatus.put("ready", "delivered");

        nextStatusStr.put("placed", cxt.getString(R.string.confirm));
        nextStatusStr.put("confirmed", cxt.getString(R.string.prepare));
        nextStatusStr.put("prepared", cxt.getString(R.string.ready));
        nextStatusStr.put("ready", cxt.getString(R.string.delivery));
        nextStatusStr.put("delivered", cxt.getString(R.string.completed));

        statusIndex.put("placed", 0);
        statusIndex.put("confirmed", 1);
        statusIndex.put("prepared", 2);
        statusIndex.put("ready", 3);
        statusIndex.put("delivered", 4);
    }
}






















