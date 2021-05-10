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
import com.myapp.qhome.main.ShippingAddressActivity;
import com.myapp.qhome.models.Address;
import com.myapp.qhome.models.Phone;
import java.util.ArrayList;

public class AddressListAdapter extends BaseAdapter {

    private ShippingAddressActivity _context;
    private ArrayList<Address> _datas = new ArrayList<>();
    private ArrayList<Address> _alldatas = new ArrayList<>();

    public AddressListAdapter(ShippingAddressActivity context){
        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Address> datas) {
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
            convertView = inflater.inflate(R.layout.item_address, parent, false);

            holder.addressBox = (TextView) convertView.findViewById(R.id.address);
            holder.addressLineBox = (TextView) convertView.findViewById(R.id.address2);
            holder.deleteButton = (ImageView) convertView.findViewById(R.id.btn_delete);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        Typeface bold, normal;

        bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");

        final Address entity = (Address) _datas.get(position);
        holder.addressBox.setText(entity.getAddress());
        holder.addressBox.setTypeface(bold);
        holder.addressLineBox.setTypeface(normal);
        if(Commons.lang.equals("ar")){
            holder.addressLineBox.setText(entity.getHouse() + "و " + entity.getStreet() + "و " + entity.getArea());
        }else {
            holder.addressLineBox.setText(entity.getArea() + ", " + entity.getStreet() + ", " + entity.getHouse());
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _context.deleteAddress(entity);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.checkoutActivity != null){
                    Commons.addrId = position;
                    _context.finish();
                }
            }
        });

        return convertView;
    }

    class CustomHolder {

        TextView addressBox, addressLineBox;
        ImageView deleteButton;
    }
}











