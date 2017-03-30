package com.call.block.group.controller;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.controller.R;
import com.call.block.group.model.Pojo;

import java.util.ArrayList;
import java.util.List;


public class CustomAdapter extends ArrayAdapter {

    private List<Pojo> pojolist = new ArrayList<Pojo>();
    private Pojo pojo;
    public Context context;
    private TextView id,number,status;
    private int ID = 1;

    public CustomAdapter(@NonNull Context context, @LayoutRes int resource, List<Pojo> pojolist) {
        super(context, R.layout.spinner_list_items, pojolist);
        this.pojolist = pojolist;
        this.context = context;
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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.spinner_list_items, parent, false);
        }
        id = (TextView) v.findViewById(R.id.number_id);
        number = (TextView) v.findViewById(R.id.number_number);
        status = (TextView) v.findViewById(R.id.number_status);

        pojo = pojolist.get(position);

        id.setText(Integer.toString(pojo.getId() + 1));
        if (pojo.getName() != null && pojo.getName().length() > 0) {
            number.setText(pojo.getName() + "(" + pojo.getNumber() + ")");
        } else {
            number.setText(pojo.getNumber());
        }
        if (pojo.getDateTime() != null && pojo.getDateTime().length() > 0) {
            status.setText(pojo.getDateTime());
        } else {
            status.setText(pojo.getAction());
        }
        return v;
    }

    public void refreshAdapter(List<Pojo> pojolistUpdate){
        if (pojolist != null) {
            pojolist.clear();
            this.pojolist.addAll(pojolistUpdate);
            this.notifyDataSetInvalidated();
        }

    }
}
