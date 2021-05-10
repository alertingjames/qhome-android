package com.myapp.qhome.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.main.CartActivity;
import com.myapp.qhome.main.OrderDetailActivity;
import com.myapp.qhome.main.PDetailActivity;
import com.myapp.qhome.main.PictureViewActivity;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.Order;
import com.myapp.qhome.models.OrderItem;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderItemListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<OrderItem> _datas = new ArrayList<>();
    private ArrayList<OrderItem> _alldatas = new ArrayList<>();
    public static DecimalFormat df = new DecimalFormat("0.00");

    public OrderItemListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<OrderItem> datas) {

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
            convertView = inflater.inflate(R.layout.item_orderitem, parent, false);

            holder.orderIDBox = (TextView) convertView.findViewById(R.id.order_number);
            holder.orderDateBox = (TextView) convertView.findViewById(R.id.order_date);
            holder.priceBox = (TextView) convertView.findViewById(R.id.priceBox);
            holder.quantityBox = (TextView) convertView.findViewById(R.id.quantityBox);
            holder.pictureBox = (RoundedImageView) convertView.findViewById(R.id.pictureBox);
            holder.caption = (TextView) convertView.findViewById(R.id.caption);
            holder.storeNameBox = (TextView) convertView.findViewById(R.id.storeNameBox);
            holder.productNameBox = (TextView) convertView.findViewById(R.id.productNameBox);
            holder.statusBox = (TextView) convertView.findViewById(R.id.statusBox);

            holder.compriceBox = (TextView)convertView.findViewById(R.id.comprice);

            holder.contactBox = (TextView) convertView.findViewById(R.id.contactBox);
            holder.producerButtonFrame = (LinearLayout)convertView.findViewById(R.id.producerButtonFrame);
            holder.cancelButton = (TextView)convertView.findViewById(R.id.btn_cancel);
            holder.progressButton = (TextView)convertView.findViewById(R.id.btn_progress);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final OrderItem entity = (OrderItem) _datas.get(position);

        Typeface normal = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");
        holder.caption.setTypeface(normal);
        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        holder.priceBox.setTypeface(bold);
        holder.contactBox.setText(entity.getContact());
        holder.contactBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + entity.getContact()));
                _context.startActivity(intent);
            }
        });
        holder.orderIDBox.setText(entity.getOrderID());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String date = dateFormat.format(new Date(Long.parseLong(entity.getDate_time())));
        holder.orderDateBox.setText(date);
        if(Commons.lang.equals("ar")){
            holder.priceBox.setText(_context.getString(R.string.qr) + " " + df.format(entity.getPrice()));
            holder.productNameBox.setText(entity.getAr_product_name());
            holder.storeNameBox.setText(entity.getAr_store_name());
            holder.quantityBox.setText(String.valueOf(entity.getQuantity()) + " " + _context.getString(R.string.x));
        }
        else {
            holder.priceBox.setText(df.format(entity.getPrice()) + " " +_context.getString(R.string.qr));
            holder.productNameBox.setText(entity.getProduct_name());
            holder.storeNameBox.setText(entity.getStore_name());
            holder.quantityBox.setText(_context.getString(R.string.x) + " " + String.valueOf(entity.getQuantity()));
        }

        if(entity.getStatus().length() > 0){
            holder.statusBox.setVisibility(View.VISIBLE);
            holder.statusBox.setText(Commons.orderStatus.statusStr.get(entity.getStatus()));
        }

        if(entity.getPicture_url().length() > 0){
            Picasso.with(_context)
                    .load(entity.getPicture_url())
                    .into(holder.pictureBox);
        }

        holder.pictureBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, PictureViewActivity.class);
                intent.putExtra("url", entity.getPicture_url());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) _context, v, _context.getString(R.string.transition));
                _context.startActivity(intent, options.toBundle());
            }
        });

        if(Commons.receivedOrdersActivity != null){
            holder.producerButtonFrame.setVisibility(View.VISIBLE);
            if(entity.getStatus().equals("delivered")){
                holder.cancelButton.setVisibility(View.GONE);
                holder.progressButton.setBackgroundResource(R.drawable.primary_stroke);
                holder.progressButton.setTextColor(_context.getColor(R.color.colorPrimary));
            }
        }else holder.producerButtonFrame.setVisibility(View.GONE);

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.receivedOrdersActivity.cancelOrderItem(entity);
            }
        });

        holder.progressButton.setText(Commons.orderStatus.nextStatusStr.get(entity.getStatus()));

        holder.progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!entity.getStatus().equals("delivered"))
                    Commons.receivedOrdersActivity.progressOrderItem(entity, Commons.orderStatus.nextStatus.get(entity.getStatus()));
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Order> orders = new ArrayList<>();
                if(Commons.ordersActivity != null){
                    orders.addAll(Commons.ordersActivity.orders);
                    for(Order order:orders){
                        if(order.getId() == entity.getOrder_id()){
                            Commons.order = order;
                            Intent intent = new Intent(_context, OrderDetailActivity.class);
                            _context.startActivity(intent);
                        }
                    }
                }else if(Commons.receivedOrdersActivity != null){
                    Commons.orderItem = entity;
                    Commons.receivedOrdersActivity.getOrder(entity);
                }
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
            for (OrderItem item : _alldatas){
                if (item instanceof OrderItem) {
                    String value = ((OrderItem) item).getOrderID().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(item);
                    }else {
                        value = ((OrderItem) item).getCategory().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(item);
                        }else {
                            value = String.valueOf(((OrderItem) item).getPrice());
                            if (value.contains(charText)) {
                                _datas.add(item);
                            }else {
                                value = ((OrderItem) item).getAr_category().toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(item);
                                }else {
                                    value = ((OrderItem) item).getStore_name().toLowerCase();
                                    if (value.contains(charText)) {
                                        _datas.add(item);
                                    }else {
                                        value = ((OrderItem) item).getAr_store_name().toLowerCase();
                                        if (value.contains(charText)) {
                                            _datas.add(item);
                                        }else {
                                            value = ((OrderItem) item).getProduct_name().toLowerCase();
                                            if (value.contains(charText)) {
                                                _datas.add(item);
                                            }else {
                                                value = ((OrderItem) item).getAr_product_name().toLowerCase();
                                                if (value.contains(charText)) {
                                                    _datas.add(item);
                                                }else {
                                                    value = String.valueOf(((OrderItem) item).getQuantity());
                                                    if (value.contains(charText)) {
                                                        _datas.add(item);
                                                    }
                                                }
                                            }
                                        }
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

        TextView caption, productNameBox, storeNameBox, contactBox, compriceBox;
        TextView orderIDBox, orderDateBox, priceBox, quantityBox, statusBox;
        TextView cancelButton, progressButton;
        RoundedImageView pictureBox;
        LinearLayout producerButtonFrame;
    }
}











