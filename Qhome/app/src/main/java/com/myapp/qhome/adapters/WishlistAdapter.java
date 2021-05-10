package com.myapp.qhome.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.qhome.R;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.main.PDetailActivity;
import com.myapp.qhome.main.WishlistActivity;
import com.myapp.qhome.models.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WishlistAdapter extends BaseAdapter {

    private WishlistActivity _context;
    private ArrayList<Product> _datas = new ArrayList<>();
    private ArrayList<Product> _alldatas = new ArrayList<>();

    public WishlistAdapter(WishlistActivity context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Product> datas) {

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
            convertView = inflater.inflate(R.layout.item_wishlist, parent, false);

            holder.picture = (ImageView) convertView.findViewById(R.id.pictureBox);
            holder.price = (TextView) convertView.findViewById(R.id.priceBox);
            holder.oldPrice= (TextView) convertView.findViewById(R.id.oldPriceBox);
            holder.oldPriceFrame= (FrameLayout) convertView.findViewById(R.id.oldPriceFrame);
            holder.delButton = (ImageView) convertView.findViewById(R.id.btn_delete);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Product entity = (Product) _datas.get(position);

        if(Commons.lang.equals("ar")) {
            if(entity.getNew_price() == 0){
                holder.oldPriceFrame.setVisibility(View.INVISIBLE);
                holder.price.setText(_context.getString(R.string.qr) + " " + String.valueOf(entity.getPrice()));
            }
            else {
                holder.oldPriceFrame.setVisibility(View.VISIBLE);
                holder.price.setText(_context.getString(R.string.qr) + " " + String.valueOf(entity.getNew_price()));
                holder.oldPrice.setText(_context.getString(R.string.qr) + " " + String.valueOf(entity.getPrice()));
            }
        }
        else {
            if(entity.getNew_price() == 0){
                holder.oldPriceFrame.setVisibility(View.INVISIBLE);
                holder.price.setText(String.valueOf(entity.getPrice()) + " " + _context.getString(R.string.qr));
            }
            else {
                holder.oldPriceFrame.setVisibility(View.VISIBLE);
                holder.price.setText(String.valueOf(entity.getNew_price()) + " " + _context.getString(R.string.qr));
                holder.oldPrice.setText(String.valueOf(entity.getPrice()) + " " + _context.getString(R.string.qr));
            }
        }

        Typeface book = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");
        holder.price.setTypeface(book);
        holder.oldPrice.setTypeface(book);

        if(entity.getPicture_url().length() > 0){
            Picasso.with(_context)
                    .load(entity.getPicture_url())
                    .into(holder.picture);
        }

        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _context.deleteProduct(entity);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.cartProduct = entity;
                Intent intent = new Intent(_context, PDetailActivity.class);
                _context.startActivity(intent);
                _context.overridePendingTransition(R.anim.bottom_in, R.anim.fade_off);
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
            for (Product product : _alldatas){

                if (product instanceof Product) {

                    String value = ((Product) product).getName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(product);
                    }else {
                        value = ((Product) product).getCategory().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(product);
                        }else {
                            value = String.valueOf(((Product) product).getPrice());
                            if (value.contains(charText)) {
                                _datas.add(product);
                            }else {
                                value = ((Product) product).getAr_name().toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(product);
                                }else {
                                    value = ((Product) product).getAr_category().toLowerCase();
                                    if (value.contains(charText)) {
                                        _datas.add(product);
                                    }else {
                                        value = ((Product) product).getDescription().toLowerCase();
                                        if (value.contains(charText)) {
                                            _datas.add(product);
                                        }else {
                                            value = ((Product) product).getAr_description().toLowerCase();
                                            if (value.contains(charText)) {
                                                _datas.add(product);
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

        ImageView picture;
        TextView price, oldPrice;
        FrameLayout oldPriceFrame;
        ImageView delButton;
    }
}
































