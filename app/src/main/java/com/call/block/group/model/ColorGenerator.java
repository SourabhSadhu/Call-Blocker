package com.call.block.group.model;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by sourabh on 17/2/17.
 */

public class ColorGenerator {

    public static int getColor() {
        Random randomGenerator = new Random();
        int red = randomGenerator.nextInt(256);
        int green = randomGenerator.nextInt(256);
        int blue = randomGenerator.nextInt(256);
        return Color.argb(255,red,green,blue);
    }

}
