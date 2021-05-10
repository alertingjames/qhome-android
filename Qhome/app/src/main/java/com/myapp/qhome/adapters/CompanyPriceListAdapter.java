package com.myapp.qhome.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.qhome.R;
import com.myapp.qhome.commons.Commons;
import com.myapp.qhome.models.CompanyPrice;

import java.util.ArrayList;

public class CompanyPriceListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<CompanyPrice> _datas = new ArrayList<>();
    private ArrayList<CompanyPrice> _alldatas = new ArrayList<>();

    public CompanyPriceListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<CompanyPrice> datas) {

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
            convertView = inflater.inflate(R.layout.item_comprice, parent, false);

            holder.priceBox = (TextView) convertView.findViewById(R.id.priceBox);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        Typeface bold, normal;

        bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");

        final CompanyPrice entity = (CompanyPrice) _datas.get(position);
        holder.priceBox.setTypeface(normal);
        holder.priceBox.setText(String.valueOf(entity.getPrice()) + " " + entity.getDescription());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.registerStoreActivity != null){
                    Commons.registerStoreActivity.priceId = entity.getId();
                    Commons.registerStoreActivity.dismissPriceLayout();
                }else if(Commons.myStoreDetailActivity != null){
                    Commons.myStoreDetailActivity.priceId = entity.getId();
                    Commons.myStoreDetailActivity.dismissPriceLayout();
                }
            }
        });

        return convertView;
    }

    class CustomHolder {

        TextView priceBox;
    }
}












