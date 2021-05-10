package com.myapp.qhome.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.myapp.qhome.R;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.main.CartActivity;
import com.myapp.qhome.main.PDetailActivity;
import com.myapp.qhome.main.PictureViewActivity;
import com.myapp.qhome.models.CartItem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartItemListAdapter extends BaseAdapter {

    private CartActivity _context;
    private ArrayList<CartItem> _datas = new ArrayList<>();
    private ArrayList<CartItem> _alldatas = new ArrayList<>();
    public static DecimalFormat df = new DecimalFormat("0.00");

    public CartItemListAdapter(CartActivity context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<CartItem> datas) {

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
            convertView = inflater.inflate(R.layout.item_cart_list, parent, false);

            holder.storeNameBox = (TextView) convertView.findViewById(R.id.storeNameBox);
            holder.productNameBox = (TextView) convertView.findViewById(R.id.productNameBox);
            holder.priceBox = (TextView) convertView.findViewById(R.id.priceBox);
            holder.quantityBox = (TextView) convertView.findViewById(R.id.quantityBox);
            holder.pictureBox = (ImageView) convertView.findViewById(R.id.pictureBox);
            holder.decreaseButton = (ImageView)convertView.findViewById(R.id.btn_decrease);
            holder.increaseButton = (ImageView) convertView.findViewById(R.id.btn_increase);
            holder.deleteButton = (ImageView) convertView.findViewById(R.id.btn_delete);
            holder.layout = (LinearLayout)convertView.findViewById(R.id.layout);

            holder.wishlistButton = (ImageView) convertView.findViewById(R.id.btn_wishlist);
            holder.okButton = (ImageView) convertView.findViewById(R.id.btn_ok);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final CartItem entity = (CartItem) _datas.get(position);

        if(Commons.lang.equals("ar")){
            holder.storeNameBox.setText(entity.getAr_store_name());
            holder.productNameBox.setText(entity.getAr_product_name());
            holder.priceBox.setText(_context.getString(R.string.qr) + " " + df.format(entity.getPrice()));
        }else {
            holder.storeNameBox.setText(entity.getStore_name());
            holder.productNameBox.setText(entity.getProduct_name());
            holder.priceBox.setText(df.format(entity.getPrice()) + " " + _context.getString(R.string.qr));
        }

        holder.quantityBox.setText(String.valueOf(entity.getQuantity()));

        holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(holder.quantityBox.getText().toString()) > 1){
                    holder.okButton.setVisibility(View.VISIBLE);
                    holder.quantityBox.setText(String.valueOf(Integer.parseInt(holder.quantityBox.getText().toString()) - 1));
                }
            }
        });

        holder.increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.okButton.setVisibility(View.VISIBLE);
                holder.quantityBox.setText(String.valueOf(Integer.parseInt(holder.quantityBox.getText().toString()) + 1));
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _context.deleteCartItem(entity);
            }
        });

        holder.wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _context.addToWishlist(entity);
            }
        });

        holder.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _context.updateCartItem(holder.okButton, entity, holder.quantityBox.getText().toString());
            }
        });

        if(entity.getPicture_url().length() > 0){
            Picasso.with(_context)
                    .load(entity.getPicture_url())
                    .error(R.drawable.noresult)
                    .placeholder(R.drawable.noresult)
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

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _context.getProductInfo(entity);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            for (CartItem cart : _alldatas){
                if (cart instanceof CartItem) {
                    String value = ((CartItem) cart).getProduct_name().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(cart);
                    }else {
                        value = ((CartItem) cart).getCategory().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(cart);
                        }else {
                            value = String.valueOf(((CartItem) cart).getPrice());
                            if (value.contains(charText)) {
                                _datas.add(cart);
                            }else {
                                value = String.valueOf(((CartItem) cart).getQuantity());
                                if (value.contains(charText)) {
                                    _datas.add(cart);
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

        ImageView pictureBox;
        TextView storeNameBox, productNameBox;
        TextView priceBox;
        TextView quantityBox;
        LinearLayout layout;
        ImageView deleteButton, decreaseButton, increaseButton, okButton, wishlistButton;
    }
}











