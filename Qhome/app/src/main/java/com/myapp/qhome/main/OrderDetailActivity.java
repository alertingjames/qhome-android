package com.myapp.qhome.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.R;
import com.myapp.qhome.base.BaseActivity;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.commons.Constants;
import com.myapp.qhome.models.CartItem;
import com.myapp.qhome.models.OrderItem;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderDetailActivity extends BaseActivity {

    TextView orderIDBox, orderDateBox, phoneBox, addressBox, addressLineBox, subTotalBox, shippingBox, totalBox, bonusBox;
    LinearLayout itemsBox, bonusFrame;
    public static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption4)).setTypeface(normal);

        ((TextView)findViewById(R.id.caption5)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption6)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption7)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption8)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption9)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption10)).setTypeface(bold);

        orderIDBox = (TextView)findViewById(R.id.order_number);
        orderDateBox = (TextView)findViewById(R.id.order_date);
        phoneBox = (TextView)findViewById(R.id.phone);
        addressBox = (TextView)findViewById(R.id.address);
        addressLineBox = (TextView)findViewById(R.id.address2);
        subTotalBox = (TextView)findViewById(R.id.subtotal_price);
        shippingBox = (TextView)findViewById(R.id.shipping_price);
        totalBox = (TextView)findViewById(R.id.total_price);
        itemsBox = (LinearLayout)findViewById(R.id.items);
        bonusFrame = (LinearLayout)findViewById(R.id.bonusFrame);
        bonusBox = (TextView)findViewById(R.id.bonus);

        if(Commons.order.getDiscount() > 0)bonusFrame.setVisibility(View.VISIBLE);

        orderIDBox.setText(Commons.order.getOrderID());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String date = dateFormat.format(new Date(Long.parseLong(Commons.order.getDate())));
        orderDateBox.setText(date);
        phoneBox.setText(Commons.order.getPhone_number());
        addressBox.setText(Commons.order.getAddress());
        addressLineBox.setText(Commons.order.getAddress_line());

        double bonus = ((Commons.order.getPrice() - Constants.SHIPPING_PRICE)*100/(100 - Commons.order.getDiscount())) * Commons.order.getDiscount()/100;
        if(Commons.lang.equals("ar")){
            subTotalBox.setText(getString(R.string.qr) + " "  + df.format(Commons.order.getPrice() - Constants.SHIPPING_PRICE + bonus));
            bonusBox.setText(getString(R.string.qr) + " "  + df.format(bonus) + "-");
            shippingBox.setText(getString(R.string.qr) + " "  + String.valueOf(Constants.SHIPPING_PRICE));
            totalBox.setText(getString(R.string.qr) + " "  + df.format(Commons.order.getPrice()));
        }else {
            subTotalBox.setText(df.format(Commons.order.getPrice() - Constants.SHIPPING_PRICE + bonus) + " " + getString(R.string.qr));
            bonusBox.setText("-" + df.format(bonus) + " " + getString(R.string.qr));
            shippingBox.setText(String.valueOf(Constants.SHIPPING_PRICE) + " " + getString(R.string.qr));
            totalBox.setText(df.format(Commons.order.getPrice()) + " " + getString(R.string.qr));
        }

        itemsBox.removeAllViews();
        for(OrderItem orderItem:Commons.order.getItems()){
            LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_order_picture, null);
            RoundedImageView picture = (RoundedImageView) view.findViewById(R.id.item_picture);
            TextView mask = (TextView)view.findViewById(R.id.txt_mask);
            if(Commons.lang.equals("ar"))mask.setText(String.valueOf(orderItem.getQuantity()) + " " + getString(R.string.x));
            else mask.setText(getString(R.string.x) + " " + String.valueOf(orderItem.getQuantity()));
            Picasso.with(getApplicationContext()).load(orderItem.getPicture_url()).into(picture);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Commons.cartItems.clear();
                    for(OrderItem orderItem: Commons.order.getItems()){
                        CartItem cartItem = new CartItem();
                        cartItem.setStore_name(orderItem.getStore_name());
                        cartItem.setAr_store_name(orderItem.getAr_store_name());
                        cartItem.setProduct_name(orderItem.getProduct_name());
                        cartItem.setAr_product_name(orderItem.getAr_product_name());
                        cartItem.setPrice(orderItem.getPrice());
                        cartItem.setQuantity(orderItem.getQuantity());
                        cartItem.setPicture_url(orderItem.getPicture_url());
                        cartItem.setStatus(orderItem.getStatus());

                        Commons.cartItems.add(cartItem);
                    }
                    Intent intent = new Intent(getApplicationContext(), CheckoutItemsActivity.class);
                    startActivity(intent);
                }
            });
            itemsBox.addView(view);
        }

    }

    public void back(View view){
        onBackPressed();
    }
}

































