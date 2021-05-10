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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.R;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.main.LoginActivity;
import com.myapp.qhome.main.MyStoreDetailActivity;
import com.myapp.qhome.models.Store;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyStoreListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Store> _datas = new ArrayList<>();
    private ArrayList<Store> _alldatas = new ArrayList<>();

    public MyStoreListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Store> datas) {

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
            convertView = inflater.inflate(R.layout.item_store_list2, parent, false);

            holder.picture = (CircleImageView) convertView.findViewById(R.id.pictureBox);
            holder.name = (TextView) convertView.findViewById(R.id.nameBox);
            holder.category = (TextView) convertView.findViewById(R.id.categoryBox);
            holder.description = (TextView) convertView.findViewById(R.id.descriptionBox);
            holder.status = (ImageView) convertView.findViewById(R.id.statusBox);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar);
            holder.ratings = (TextView) convertView.findViewById(R.id.ratings);
            holder.likes = (TextView) convertView.findViewById(R.id.likes);
            holder.reviews = (TextView) convertView.findViewById(R.id.reviews);

            holder.reviewsCap = (TextView) convertView.findViewById(R.id.reviews_cap);
            holder.moreCap = (TextView) convertView.findViewById(R.id.more_cap);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Store entity = (Store) _datas.get(position);


        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        holder.name.setTypeface(bold);

        Typeface book = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");
        holder.category.setTypeface(book);
        holder.description.setTypeface(book);

        if(Commons.lang.equals("ar")) {
            String aCat2 = "";
            if(entity.getAr_category2().length() > 0) aCat2 = entity.getAr_category2() + "و ";
            holder.name.setText(entity.getAr_name());
            holder.category.setText(aCat2 + entity.getAr_category());
            holder.description.setText(StringEscapeUtils.unescapeJava(entity.getAr_description()));
        }
        else {
            String cat2 = "";
            if(entity.getCategory2().length() > 0) cat2 = ", " + entity.getCategory2();
            holder.name.setText(entity.getName());
            holder.category.setText(entity.getCategory() + cat2);
            holder.description.setText(StringEscapeUtils.unescapeJava(entity.getDescription()));
        }

        holder.likes.setText(String.valueOf(entity.getLikes()));
        holder.ratingBar.setRating(entity.getRatings());
        holder.ratings.setText(String.valueOf(entity.getRatings()));
        holder.reviews.setText(String.valueOf(entity.getReviews()));

        holder.reviewsCap.setTypeface(book);
//        holder.moreCap.setTypeface(book);

        if(entity.getStatus().equals("approved"))holder.status.setImageResource(R.drawable.ic_checked);
        else if(entity.getStatus().equals("declined"))holder.status.setImageResource(R.drawable.cancelicon_marron);
        else holder.status.setVisibility(View.GONE);

        if(entity.getLogo_url().length() > 0){
            Picasso.with(_context)
                    .load(entity.getLogo_url())
                    .into(holder.picture);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.store = entity;
                Intent intent = new Intent(_context, MyStoreDetailActivity.class);
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
            for (Store store : _alldatas){

                if (store instanceof Store) {

                    String value = ((Store) store).getName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(store);
                    }else {
                        value = ((Store) store).getCategory().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(store);
                        }else {
                            value = ((Store) store).getAr_name().toLowerCase();
                            if (value.contains(charText)) {
                                _datas.add(store);
                            }else {
                                value = ((Store) store).getAr_category().toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(store);
                                }else {
                                    value = ((Store) store).getCategory2().toLowerCase();
                                    if (value.contains(charText)) {
                                        _datas.add(store);
                                    }else {
                                        value = ((Store) store).getAr_category2().toLowerCase();
                                        if (value.contains(charText)) {
                                            _datas.add(store);
                                        }else {
                                            value = ((Store) store).getDescription().toLowerCase();
                                            if (value.contains(charText)) {
                                                _datas.add(store);
                                            }else {
                                                value = ((Store) store).getAr_description().toLowerCase();
                                                if (value.contains(charText)) {
                                                    _datas.add(store);
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

        CircleImageView picture;
        ImageView status;
        TextView name, category, description, likes, ratings, reviews, reviewsCap, moreCap;
        RatingBar ratingBar;
    }
}

































