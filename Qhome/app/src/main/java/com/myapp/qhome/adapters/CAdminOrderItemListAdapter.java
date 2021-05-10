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
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.R;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.main.PictureViewActivity;
import com.myapp.qhome.models.OrderItem;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CAdminOrderItemListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<OrderItem> _datas = new ArrayList<>();
    private ArrayList<OrderItem> _alldatas = new ArrayList<>();
    public static DecimalFormat df = new DecimalFormat("0.00");

    public CAdminOrderItemListAdapter(Context context){

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
            convertView = inflater.inflate(R.layout.item_comorder, parent, false);

            holder.orderDateBox = (TextView) convertView.findViewById(R.id.order_date);
            holder.priceBox = (TextView) convertView.findViewById(R.id.priceBox);
            holder.quantityBox = (TextView) convertView.findViewById(R.id.quantityBox);
            holder.pictureBox = (RoundedImageView) convertView.findViewById(R.id.pictureBox);
            holder.caption = (TextView) convertView.findViewById(R.id.caption);
            holder.storeNameBox = (TextView) convertView.findViewById(R.id.storeNameBox);
            holder.compriceBox = (TextView)convertView.findViewById(R.id.comprice);
            holder.contactBox = (TextView) convertView.findViewById(R.id.contactBox);
            holder.contactBox2 = (TextView) convertView.findViewById(R.id.contactBox2);

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
        holder.contactBox2.setText(entity.getProducer_contact());
        holder.contactBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + entity.getProducer_contact()));
                _context.startActivity(intent);
            }
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String date = dateFormat.format(new Date(Long.parseLong(entity.getDate_time())));
        holder.orderDateBox.setText(date);
        if(Commons.lang.equals("ar")){
            holder.priceBox.setText(_context.getString(R.string.qr) + " " + df.format(entity.getPrice()));
            holder.storeNameBox.setText(entity.getAr_store_name());
            holder.quantityBox.setText(String.valueOf(entity.getQuantity()) + " " + _context.getString(R.string.x));
        }
        else {
            holder.priceBox.setText(df.format(entity.getPrice()) + " " +_context.getString(R.string.qr));
            holder.storeNameBox.setText(entity.getStore_name());
            holder.quantityBox.setText(_context.getString(R.string.x) + " " + String.valueOf(entity.getQuantity()));
        }

        if(entity.getCompriceStr().length() > 0){
            holder.compriceBox.setVisibility(View.VISIBLE);
            holder.compriceBox.setText(entity.getCompriceStr());
        }else {
            holder.compriceBox.setVisibility(View.GONE);
        }

        holder.compriceBox.setTypeface(normal);

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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.orderItem = entity;
                Commons.cAdminActivity.getOrder(entity);
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

        TextView caption, storeNameBox, contactBox, contactBox2, compriceBox;
        TextView orderDateBox, priceBox, quantityBox;
        RoundedImageView pictureBox;
    }
}











