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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.myapp.qhome.R;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.main.MyStoreDetailActivity;
import com.myapp.qhome.main.StoreDetailActivity;
import com.myapp.qhome.models.Store;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Store> _datas = new ArrayList<>();
    private ArrayList<Store> _alldatas = new ArrayList<>();

    public StoreListAdapter(Context context){

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
            convertView = inflater.inflate(R.layout.item_store_list, parent, false);

            holder.picture = (ImageView) convertView.findViewById(R.id.pictureBox);
            holder.name = (TextView) convertView.findViewById(R.id.nameBox);
            holder.likeButton = (ImageButton) convertView.findViewById(R.id.btn_like);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Store entity = (Store) _datas.get(position);

        if(Commons.lang.equals("ar")) holder.name.setText(entity.getAr_name());
        else holder.name.setText(entity.getName());

        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        holder.name.setTypeface(bold);

        if(entity.isLiked())holder.likeButton.setBackgroundResource(R.drawable.ic_liked);
        else if(!entity.isLiked())holder.likeButton.setBackgroundResource(R.drawable.ic_like);

        if(Commons.thisUser == null)holder.likeButton.setVisibility(View.GONE);
        else {
            if(Commons.thisUser.get_idx() == entity.getUserId())holder.likeButton.setVisibility(View.GONE);
            else holder.likeButton.setVisibility(View.VISIBLE);
        }

        Picasso.with(_context).setLoggingEnabled(true);

        if(entity.getLogo_url().length() > 0){
            View finalConvertView = convertView;
            Picasso.with(_context)
                    .load(entity.getLogo_url()).error(R.drawable.noresult)
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
                if(Commons.homeActivity != null){
                    if(entity.isLiked())Commons.homeActivity.unLikeStore(String.valueOf(entity.getIdx()));
                    else Commons.homeActivity.likeStore(String.valueOf(entity.getIdx()));
                    Commons.homeActivity.likeButton = holder.likeButton;
                    Commons.homeActivity.storeId = entity.getIdx();
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.store = entity;
                if(Commons.thisUser != null){
                    if(entity.getUserId() == Commons.thisUser.get_idx()){
                        Intent intent = new Intent(_context, MyStoreDetailActivity.class);
                        _context.startActivity(intent);
                    }else {
                        Intent intent = new Intent(_context, StoreDetailActivity.class);
                        _context.startActivity(intent);
                    }
                }
                else {
                    Intent intent = new Intent(_context, StoreDetailActivity.class);
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
        ImageButton likeButton;
    }
}
































