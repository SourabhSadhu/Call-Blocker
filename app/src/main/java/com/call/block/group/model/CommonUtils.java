package com.call.block.group.model;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sourabh on 5/4/17.
 * author: Sourabh
 * contains: basic methods needed by every class
 */

public class CommonUtils {

    public static boolean checkDuplicate(List<PojoCallLogData> list, PojoCallLogData obj) {

        if(null != list) {
            for (PojoCallLogData pojoCallLogData : list) {
                if (null != pojoCallLogData.getNumber() && null != obj.getNumber() && pojoCallLogData.getNumber().contains(obj.getNumber())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int getColor() {
        Random randomGenerator = new Random();
        int red = randomGenerator.nextInt(256);
        int green = randomGenerator.nextInt(256);
        int blue = randomGenerator.nextInt(256);
        return Color.argb(255,red,green,blue);
    }

    public static String nameCred(String name){
        if(name.contains(" ")){
            String[] seperated = name.split(" ",2);
            return seperated[0].substring(0,1).toUpperCase() + seperated[1].substring(0,1).toUpperCase();
        }
        else
            return name.substring(0,1).toUpperCase();
    }
}
