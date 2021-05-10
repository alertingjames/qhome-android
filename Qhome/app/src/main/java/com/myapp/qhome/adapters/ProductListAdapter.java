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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myapp.qhome.R;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.main.MyProductDetailActivity;
import com.myapp.qhome.main.MyStoreDetailActivity;
import com.myapp.qhome.main.PDetailActivity;
import com.myapp.qhome.main.ProductDetailActivity;
import com.myapp.qhome.models.Product;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Product> _datas = new ArrayList<>();
    private ArrayList<Product> _alldatas = new ArrayList<>();

    public ProductListAdapter(Context context){

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
            convertView = inflater.inflate(R.layout.item_product_list, parent, false);

            holder.picture = (ImageView) convertView.findViewById(R.id.pictureBox);
            holder.price = (TextView) convertView.findViewById(R.id.priceBox);
            holder.oldPrice= (TextView) convertView.findViewById(R.id.oldPriceBox);
            holder.oldPriceFrame= (FrameLayout) convertView.findViewById(R.id.oldPriceFrame);
            holder.likeButton = (ImageButton) convertView.findViewById(R.id.btn_like);
            holder.deleteButton = (ImageView) convertView.findViewById(R.id.btn_delete);

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

        if(entity.isLiked())holder.likeButton.setBackgroundResource(R.drawable.ic_liked);
        else if(!entity.isLiked())holder.likeButton.setBackgroundResource(R.drawable.ic_like);

        if(Commons.thisUser != null){
            if(Commons.thisUser.get_idx() == entity.getUserId()){
                holder.likeButton.setVisibility(View.GONE);
                holder.deleteButton.setVisibility(View.VISIBLE);
            }else{
                holder.deleteButton.setVisibility(View.GONE);
            }
        }
        else {
            holder.likeButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        if(entity.getPicture_url().length() > 0){
            View finalConvertView = convertView;
            Picasso.with(_context)
                    .load(entity.getPicture_url())
                    .into(holder.picture, new Callback() {
                        @Override
                        public void onSuccess() {
                            ((ProgressBar) finalConvertView.findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            ((ProgressBar) finalConvertView.findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                        }
                    });
        }

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.storeDetailActivity != null){
                    if(entity.isLiked())Commons.storeDetailActivity.unsaveProduct(String.valueOf(entity.getIdx()));
                    else Commons.storeDetailActivity.saveProduct(String.valueOf(entity.getIdx()));
                    Commons.storeDetailActivity.likeButton = holder.likeButton;
                    Commons.storeDetailActivity.productId = entity.getIdx();
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.myStoreDetailActivity != null){
                    Commons.myStoreDetailActivity.deleteProduct(entity);
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.product = entity;
                if(Commons.thisUser != null){
                    if(entity.getUserId() == Commons.thisUser.get_idx()){
                        Intent intent = new Intent(_context, ProductDetailActivity.class);
                        intent.putExtra("from", "mine");
                        _context.startActivity(intent);
                    }else {
                        Intent intent = new Intent(_context, ProductDetailActivity.class);
                        intent.putExtra("compriceid", Commons.storeDetailActivity.comDeliveryPriceId);
                        _context.startActivity(intent);
                    }
                }
                else {
                    Intent intent = new Intent(_context, ProductDetailActivity.class);
                    intent.putExtra("compriceid", Commons.storeDetailActivity.comDeliveryPriceId);
                    _context.startActivity(intent);
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
        ImageButton likeButton;
        ImageView deleteButton;
    }
}
































