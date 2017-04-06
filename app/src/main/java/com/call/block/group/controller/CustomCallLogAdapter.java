package com.call.block.group.controller;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.call.block.group.model.CallBlockNumberType;
import com.call.block.group.model.CommonUtils;
import com.call.block.group.model.PojoCallLogData;
import com.controller.R;
import com.call.block.group.model.Pojo;

import java.util.List;


public class CustomCallLogAdapter extends ArrayAdapter {

    private Context context;
    private PojoCallLogData pojo;
    private List<PojoCallLogData> pojolist;

    private RelativeLayout contact_image;
    private TextView contact_image_textview,contact_name,contact_number,contact_action,contact_log_date,contact_log_time;
    private LinearLayout contact_log_date_time;

    public CustomCallLogAdapter(@NonNull Context context, @LayoutRes int resource, List<PojoCallLogData> pojolist) {
        super(context, resource, pojolist);
        this.context = context;
        this.pojo = new PojoCallLogData();
        this.pojolist = pojolist;
    }

    @Override
    public int getCount() {
        if(pojolist != null) {
            return pojolist.size();
        }
        return 0;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return pojolist.get(position);

    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_contact_list, parent, false);
        }

        contact_image = (RelativeLayout) v.findViewById(R.id.contact_image);
        contact_image_textview = (TextView) v.findViewById(R.id.contact_image_textview);
        contact_name = (TextView) v.findViewById(R.id.contact_name);
        contact_number = (TextView) v.findViewById(R.id.contact_number);
        contact_action = (TextView) v.findViewById(R.id.contact_action);
        contact_log_date_time = (LinearLayout) v.findViewById(R.id.contact_log_date_time);
        contact_log_date = (TextView) v.findViewById(R.id.contact_log_date);
        contact_log_time = (TextView) v.findViewById(R.id.contact_log_time);

        pojo = pojolist.get(position);

        contact_image.setBackgroundColor(CommonUtils.getColor());

        if(null != pojo.getName()) {
            contact_image_textview.setText(nameCred(pojo.getName()));
            contact_name.setText(pojo.getName());
        }
        if(null != pojo.getNumber())
            contact_number.setText(pojo.getNumber());
        if(null != pojo.getCall_date())
            contact_action.setText(pojo.getDate_time());
        if(null != pojo.getType() && null != pojo.getDuration()) {
            contact_log_date.setText(pojo.getType());
            contact_log_time.setText(pojo.getDuration());
        }

        return v;
    }

    public String nameCred(String name){
        if(name.contains(" ")){
            String[] seperated = name.split(" ",2);
            return seperated[0].substring(0,1).toUpperCase() + seperated[1].substring(0,1).toUpperCase();
        }
        else
            return name.substring(0,1).toUpperCase();
    }


    public void refreshAdapter(List<PojoCallLogData> log) {
        if (pojolist != null && log != null) {
            pojolist.clear();
            this.pojolist.addAll(log);
            this.notifyDataSetInvalidated();
        }
    }
}
