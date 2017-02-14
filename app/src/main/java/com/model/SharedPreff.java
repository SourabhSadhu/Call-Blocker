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
    private Gson gson;
    private String name;

    public SharedPreff(Context ctx, String name) {
        super(ctx);
        this.thisContext = ctx;
        gson = new Gson();
        this.name = name;
        mPrefs = getSharedPreferences(name, thisContext.MODE_PRIVATE);
        //getApplicationContext().
    }

    public void SaveSerialize(List<Pojo> MyObject) {
        String json = gson.toJson(MyObject);
        Log.d("Shared Preference", "Saving Data" + json);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(name, json);
        prefsEditor.commit();
    }

    public List<Pojo> Retreive(String name) {
        String json = mPrefs.getString(name, "");
        Log.d("Shared Preference", "Retreived Data" + json);
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

    public void UpdateList(Pojo p,String name) {
        List<Pojo> updateList = Retreive(name);
        if (updateList != null){
        }else{
            updateList = new ArrayList<Pojo>();
        }
        updateList.add(p);
        SaveSerialize(updateList);
    }

    public void DeleteNumber(int position, String name) {
        List<Pojo> list = Retreive(name);
        list.remove(position);
        SaveSerialize(list);
        PrintList(list);
    }

    public void PrintList(List<Pojo> list){
        Log.d("Shared Preference","For Loop Data");
        for (int i = 0; i < list.size(); i++) {
            Log.d("For Loop Data", "Mobile Number " + list.get(i).getNumber()+":Action "+list.get(i).getAction());
        }
    }

    public void ClearAll(String name){
//        prefsEditor.putString(name, null);
//        prefsEditor.commit();
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove(name).commit();
    }
}
