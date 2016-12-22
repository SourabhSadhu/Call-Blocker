package com.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.model.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by SourabhSadhu on 13-12-2016.
 */

public class SharedPreff extends ContextWrapper {

    Context thisContext;
    SharedPreferences mPrefs;

    public SharedPreff(Context ctx) {
        super(ctx);
        this.thisContext = ctx;
        mPrefs = getSharedPreferences("MyObject", thisContext.MODE_PRIVATE);
        //getApplicationContext().
    }

    public void SaveSerialize(List<Pojo> MyObject) {
        Log.d("Shared Preference", "preference" + mPrefs.getClass());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(MyObject);
        Log.d("Shared Preference", "Saving Data" + json);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();
    }

    public List<Pojo> Retreive() {
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        Log.d("Shared Preference", "Retreived Data" + json);
        Type listType = new TypeToken<ArrayList<Pojo>>() {
        }.getType();
        List<Pojo> obj = (List<Pojo>) gson.fromJson(json, listType);
        int id = 1;
        if(obj!=null) {
            for (int i = 0; i < obj.size(); i++) {
                obj.get(i).setId(id);
                id++;
            }
        }
        return obj;
    }

    public void UpdateList(Pojo p) {
        List<Pojo> updateList = Retreive();
        if (updateList != null){
            updateList.add(p);
            SaveSerialize(updateList);
        }
    }

    public void DeleteNumber(int position) {
        List<Pojo> list = Retreive();
        list.remove(position);
        SaveSerialize(list);
        PrintList(list);
    }

    public void PrintList(List<Pojo> list){
        Log.d("Shared Preference","For Loop Data");
        for (int i = 0; i < list.size(); i++) {
            Log.d("For Loop Data", "Mobile Number " + list.get(i).getNumber()+":Action "+list.get(i).getAction());
        }
        Log.d("Shared Preference","List Iterator Data");
    }
}
