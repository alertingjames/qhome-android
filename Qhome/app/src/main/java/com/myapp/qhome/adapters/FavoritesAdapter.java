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
import com.myapp.qhome.main.FavoritesActivity;
import com.myapp.qhome.main.PDetailActivity;
import com.myapp.qhome.main.StoreDetailActivity;
import com.myapp.qhome.main.WishlistActivity;
import com.myapp.qhome.models.Product;
import com.myapp.qhome.models.Store;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoritesAdapter extends BaseAdapter {

    private FavoritesActivity _context;
    private ArrayList<Store> _datas = new ArrayList<>();
    private ArrayList<Store> _alldatas = new ArrayList<>();

    public FavoritesAdapter(FavoritesActivity context){

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
            convertView = inflater.inflate(R.layout.item_favorite, parent, false);

            holder.picture = (ImageView) convertView.findViewById(R.id.pictureBox);
            holder.name = (TextView) convertView.findViewById(R.id.nameBox);
            holder.delButton = (ImageView) convertView.findViewById(R.id.btn_delete);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Store entity = (Store) _datas.get(position);

        if(Commons.lang.equals("ar")) holder.name.setText(entity.getAr_name());
        else holder.name.setText(entity.getName());

        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        holder.name.setTypeface(bold);

        if(entity.getLogo_url().length() > 0){
            Picasso.with(_context)
                    .load(entity.getLogo_url())
                    .into(holder.picture);
        }

        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _context.deleteFavStore(entity);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.store = entity;
                Intent intent = new Intent(_context, StoreDetailActivity.class);
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

        ImageView picture;
        TextView name;
        ImageView delButton;
    }
}
































