package com.myapp.qhome.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.R;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.main.CartActivity;
import com.myapp.qhome.main.CheckoutActivity;
import com.myapp.qhome.main.OrderDetailActivity;
import com.myapp.qhome.main.PictureViewActivity;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Order;
import com.myapp.qhome.models.OrderItem;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Order> _datas = new ArrayList<>();
    private ArrayList<Order> _alldatas = new ArrayList<>();
    public static DecimalFormat df = new DecimalFormat("0.00");

    public OrderListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Order> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public Object getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.item_order, parent, false);

            holder.orderIDBox = (TextView) convertView.findViewById(R.id.order_number);
            holder.orderDateBox = (TextView) convertView.findViewById(R.id.order_date);
            holder.priceBox = (TextView) convertView.findViewById(R.id.order_price);
            holder.cancelButton = (TextView) convertView.findViewById(R.id.btn_cancel);
            holder.reorderButton = (TextView) convertView.findViewById(R.id.btn_reorder);
            holder.itemsBox = (LinearLayout)convertView.findViewById(R.id.items);
            holder.caption1 = (TextView) convertView.findViewById(R.id.caption1);
            holder.caption2 = (TextView) convertView.findViewById(R.id.caption2);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Order entity = (Order) _datas.get(position);

        Typeface normal = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");
        holder.caption1.setTypeface(normal);
        holder.caption2.setTypeface(normal);
        holder.orderIDBox.setText(entity.getOrderID());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String date = dateFormat.format(new Date(Long.parseLong(entity.getDate())));
        holder.orderDateBox.setText(date);
        if(Commons.lang.equals("ar"))holder.priceBox.setText(_context.getString(R.string.qr) + " " + df.format(entity.getPrice()));
        else holder.priceBox.setText(df.format(entity.getPrice()) + " " +_context.getString(R.string.qr));

        holder.itemsBox.removeAllViews();
        for(OrderItem orderItem:entity.getItems()){
            LayoutInflater layoutInflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_order_picture, null);
            RoundedImageView picture = (RoundedImageView) view.findViewById(R.id.item_picture);
            TextView mask = (TextView)view.findViewById(R.id.txt_mask);
            if(Commons.lang.equals("ar"))mask.setText(String.valueOf(orderItem.getQuantity()) + " " + _context.getString(R.string.x));
            else mask.setText(_context.getString(R.string.x) + " " + String.valueOf(orderItem.getQuantity()));
            Picasso.with(_context).load(orderItem.getPicture_url()).into(picture);
            holder.itemsBox.addView(view);
        }

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons.ordersActivity != null)Commons.ordersActivity.deleteOrder(entity);
            }
        });

        holder.reorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons.ordersActivity != null){
                    Commons.ordersActivity.toCheckOut(entity);
                }
            }
        });

        holder.itemsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.order = entity;
                Intent intent = new Intent(_context, OrderDetailActivity.class);
                _context.startActivity(intent);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.order = entity;
                Intent intent = new Intent(_context, OrderDetailActivity.class);
                _context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();
        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {
            for (Order order : _alldatas){
                if (order instanceof Order) {
                    String value = ((Order) order).getOrderID().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(order);
                    }else {
                        value = ((Order) order).getAddress().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(order);
                        }else {
                            value = String.valueOf(((Order) order).getPrice());
                            if (value.contains(charText)) {
                                _datas.add(order);
                            }else {
                                value = ((Order) order).getAddress_line().toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(order);
                                }else {
                                    value = ((Order) order).getPhone_number().toLowerCase();
                                    if (value.contains(charText)) {
                                        _datas.add(order);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        TextView caption1, caption2, cancelButton, reorderButton;
        TextView orderIDBox, orderDateBox, priceBox;
        LinearLayout itemsBox;
    }
}











