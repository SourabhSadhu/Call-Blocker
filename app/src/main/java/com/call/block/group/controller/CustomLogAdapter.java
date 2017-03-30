package com.call.block.group.controller;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.controller.R;
import com.call.block.group.model.ColorGenerator;
import com.call.block.group.model.Pojo;

import java.util.List;

/**
 * Created by sourabh on 17/2/17.
 */

public class CustomLogAdapter extends ArrayAdapter {

    private Context context;
    private Pojo pojo;
    private List<Pojo> pojolist;

    private RelativeLayout contact_image;
    private TextView contact_image_textview,contact_name,contact_number,contact_action,contact_log_date,contact_log_time;
    private LinearLayout contact_log_date_time;

    public CustomLogAdapter(@NonNull Context context, @LayoutRes int resource, List<Pojo> pojolist) {
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

        contact_image.setBackgroundColor(ColorGenerator.getColor());
        if(null != pojo.getName()) {
            contact_image_textview.setText(nameCred(pojo.getName()));
            contact_name.setText(pojo.getName());
        }
        if(null != pojo.getNumber())
        contact_number.setText(pojo.getNumber());
        if(null != pojo.getAction())
        contact_action.setText(pojo.getAction()+" for "+pojo.getBlock_action());
        if(null != pojo.getDateTime() && pojo.getDateTime().length()>10) {
            contact_log_date.setText(pojo.getDateTime().substring(0, 5));
            contact_log_time.setText(pojo.getDateTime().substring(6));
        }else{
            try {
                contact_log_date.setVisibility(View.GONE);
                contact_log_time.setVisibility(View.GONE);
                View checkImage = v.findViewById(R.id.dynamic_image);
                if(checkImage == null) {
                    ImageView action_img = new ImageView(context);
                    action_img.setLayoutParams(new android.view.ViewGroup.LayoutParams(100, 100));
                    action_img.setId(R.id.dynamic_image);
//                    action_img.setMaxHeight(40);
//                    action_img.setMaxWidth(40);
                    if (pojo.getAction().equals("Silent"))
                        action_img.setImageResource(R.mipmap.ic_menu_mute);
                    else
                        action_img.setImageResource(R.mipmap.ic_menu_block);
                    // Adds the view to the layout
                    contact_log_date_time.addView(action_img);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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


    public void refreshAdapter(List<Pojo> log) {
        if (pojolist != null && log != null) {
            pojolist.clear();
            this.pojolist.addAll(log);
            this.notifyDataSetInvalidated();
        }
    }
}
