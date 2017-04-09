package com.call.block.group.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
//import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SourabhSadhu on 13-12-2016.
 */

public class SharedPreff extends ContextWrapper {

    Context thisContext;
    SharedPreferences mPrefs;
    private Gson gson;

    public SharedPreff(Context ctx) {
        super(ctx);
        this.thisContext = ctx;
        gson = new Gson();
        mPrefs = getSharedPreferences("", thisContext.MODE_PRIVATE);
    }

    public void SaveSerialize(List<Pojo> MyObject,String name, String value) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if(null != MyObject ) {
            String json = gson.toJson(MyObject);
            Log.d("Shared Preference", "Saving Data" + json);
            prefsEditor.putString(name, json);
            PrintList(MyObject);
        }else
            prefsEditor.putString(name,"");
        prefsEditor.commit();
    }

    public List<Pojo> Retreive(String name) {
        String json = mPrefs.getString(name, "");
        Log.d("Shared Preference", "Retreived Data List" + json);
        Type listType = new TypeToken<ArrayList<Pojo>>() {
        }.getType();
        List<Pojo> obj = (List<Pojo>) gson.fromJson(json, listType);

        if(obj!=null) {
            for (int i = 0; i < obj.size(); i++) {
                obj.get(i).setId(i);
            }
        }
        return obj;
    }

    public Pojo Retreive(int position,String name) {
        String json = mPrefs.getString(name, "");
        Log.d("Shared Preference", "Retreived Data Object" + json);
        Type listType = new TypeToken<ArrayList<Pojo>>() {
        }.getType();
        List<Pojo> obj = (List<Pojo>) gson.fromJson(json, listType);

        if(obj == null) {
            return null;
        }else {
            for (int i = 0; i < obj.size(); i++) {
                obj.get(i).setId(i);
            }
            return position <= obj.size() ? obj.get(position) : null;
        }
    }

    public void UpdateList(Pojo p,String name) {
        List<Pojo> updateList = Retreive(name);
        if (updateList == null) {
            updateList = new ArrayList<Pojo>();
        }
        List ascendingList = new ArrayList();
        ascendingList.add(0, p);
        int indexSize = updateList.size();

        for (int iter = 1; iter <= indexSize; iter++) {
            ascendingList.add(iter, updateList.get(iter - 1));
        }
        SaveSerialize(ascendingList,name,null);
    }

    public void DeleteNumber(int position, String name) {
        List<Pojo> list = Retreive(name);
        list.remove(position);
        SaveSerialize(list,name,"");
    }

    public void EditList(String name,int position, Pojo p){
        List<Pojo> list = Retreive(name);
        PrintList(list);
        list.get(position).setName(p.getName());
        list.get(position).setNumber(p.getNumber());
        list.get(position).setAction(p.getAction());
        list.get(position).setBlock_action(p.getBlock_action());
        PrintList(list);
        SaveSerialize(list,name,"");

        String className = new Exception().getStackTrace()[0].getClassName();
        String className1 = new Exception().getStackTrace()[1].getClassName();
        Log.i("Class ",className);
        Log.i("Class ",className1);
    }

    public void PrintList(List<Pojo> list){
        Log.d("Shared Preference","For Loop Data");
        for (int i = 0; i < list.size(); i++) {
            Log.d("For Loop Data", "Mobile Number " + list.get(i).getNumber()+":Action "+list.get(i).getAction());
        }
    }

    public void PrintList(Pojo list){
        Log.d("For Loop Data", "Mobile Number " + list.getNumber()+":Action "+list.getAction());
    }

    public void ClearAll(String name){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove(name).commit();
    }

    public String getString(String key, String def) {
        String json = mPrefs.getString(key, "");
        Log.d("Shared Preference", "Retreived Data Object" + json);
        Type stringType = new TypeToken<String>() {}.getType();
        String obj = (String) gson.fromJson(json, stringType);
        return obj;
    }

    public void putString(String name, String value){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if(null != value){
            prefsEditor.putString(name, value);
            Log.e("putString - Printlist","Data " + name + " " +value);
        }else
            prefsEditor.putString(name,"");
        prefsEditor.commit();
    }

}
