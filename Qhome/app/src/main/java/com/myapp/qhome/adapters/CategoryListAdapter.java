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
import com.myapp.qhome.main.StoreListActivity;
import com.myapp.qhome.models.Category;
import com.myapp.qhome.models.Store;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Category> _datas = new ArrayList<>();
    private ArrayList<Category> _alldatas = new ArrayList<>();

    public CategoryListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Category> datas) {

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
            convertView = inflater.inflate(R.layout.item_category_list, parent, false);

            holder.picture = (RoundedImageView) convertView.findViewById(R.id.pictureBox);
            holder.name = (TextView) convertView.findViewById(R.id.nameBox);
            holder.tint = (View)convertView.findViewById(R.id.tintView);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Category entity = (Category) _datas.get(position);

        holder.name.setText(entity.getName());
        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        holder.name.setTypeface(bold);

        if(entity.getIdx() == 1)holder.tint.setBackgroundResource(R.drawable.trans_round_cyan_fill);
        else if(entity.getIdx() == 2)holder.tint.setBackgroundResource(R.drawable.trans_round_green_fill);
        else if(entity.getIdx() == 3)holder.tint.setBackgroundResource(R.drawable.trans_round_yellow_fill);
        else if(entity.getIdx() == 4)holder.tint.setBackgroundResource(R.drawable.trans_round_orange_fill);
        else if(entity.getIdx() == 5)holder.tint.setBackgroundResource(R.drawable.trans_round_blue_fill);
        else if(entity.getIdx() == 6)holder.tint.setBackgroundResource(R.drawable.trans_round_red_fill);

        if(entity.getLogo_url().length() > 0){
            Picasso.with(_context)
                    .load(entity.getLogo_url())
                    .into(holder.picture);
        }else {
            Picasso.with(_context)
                    .load(entity.getLogo_res())
                    .into(holder.picture);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, StoreListActivity.class);
                intent.putExtra("category", entity.getName());
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
            Commons.categoryActivity.others.setVisibility(View.VISIBLE);
        }else {
            for (Category category : _alldatas){

                if (category instanceof Category) {

                    String value = ((Category) category).getName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(category);
                        String othersStr = "Others";
                        if(!othersStr.toLowerCase().contains(charText)){
                            Commons.categoryActivity.others.setVisibility(View.GONE);
                        }else {
                            Commons.categoryActivity.others.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        RoundedImageView picture;
        TextView name;
        View tint;
    }
}
































