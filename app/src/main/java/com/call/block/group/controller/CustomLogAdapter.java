package com.call.block.group.controller;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.call.block.group.model.CommonUtils;
import com.call.block.group.R;
import com.call.block.group.model.Pojo;

import java.util.List;

/**
 * Created by sourabh on 17/2/17.
 * This adapter creates view for Main activity and Log activity
 */

class CustomLogAdapter extends ArrayAdapter {

    private Context context;
    private Pojo pojo;
    private List<Pojo> pojolist;

    CustomLogAdapter(@NonNull Context context, @LayoutRes int resource, List<Pojo> pojolist) {
        super(context, resource, pojolist);
        this.context = context;
        this.pojo = new Pojo();
        this.pojolist = pojolist;
    }

    @Override
    public int getCount() {
        if(pojolist != null) {
            return pojolist.size();
        }
        return 0;
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_contact_list, parent, false);
        }

        RelativeLayout contact_image = (RelativeLayout) v.findViewById(R.id.contact_image);
        TextView contact_image_textview = (TextView) v.findViewById(R.id.contact_image_textview);
        TextView contact_name = (TextView) v.findViewById(R.id.contact_name);
        TextView contact_number = (TextView) v.findViewById(R.id.contact_number);
        TextView contact_action = (TextView) v.findViewById(R.id.contact_action);
        ImageView img_type = (ImageView) v.findViewById(R.id.img_type);

        pojo = pojolist.get(position);

        contact_image.setBackgroundColor(CommonUtils.getColor());
        if(null != pojo.getName()) {
            contact_image_textview.setText(CommonUtils.nameCred(pojo.getName()));
            contact_name.setText(pojo.getName());
        }
        if(null != pojo.getNumber())
        contact_number.setText(pojo.getNumber());

        String desc;
        if(null != pojo.getAction()){
            desc = pojo.getAction()+" for "+pojo.getBlock_action();
            if(null != pojo.getDateTime() && pojo.getDateTime().length()>10) {
                desc = pojo.getAction() + " at " + pojo.getDateTime();
            }
            contact_action.setText(desc);
        }

        if (pojo.getAction().equals("Silent"))
            img_type.setImageResource(R.mipmap.ic_menu_mute);
        else if (pojo.getAction().equals("Block"))
            img_type.setImageResource(R.mipmap.ic_menu_block);

        return v;
    }


    void refreshAdapter(List<Pojo> log) {
        if (pojolist != null && log != null) {
            pojolist.clear();
            this.pojolist.addAll(log);
            this.notifyDataSetInvalidated();
        }
    }
}
