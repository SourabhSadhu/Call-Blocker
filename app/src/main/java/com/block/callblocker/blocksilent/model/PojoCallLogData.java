package com.block.callblocker.blocksilent.model;

import java.util.Date;

/**
 * Created by sourabh on 5/4/17.
 */

public class PojoCallLogData {

    private String name;
    private String number;
    private String type;
    private String location;
    private String date_time;
    private Date call_date;
    private int duration;
    private int typeInt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getCall_date() { return this.call_date;}

    public void setCall_date(Date call_date) { this.call_date = call_date;}

    public int getTypeInt() {
        return typeInt;
    }

    public void setTypeInt(int typeInt) {
        this.typeInt = typeInt;
    }
}
