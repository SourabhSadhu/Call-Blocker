package com.model;

/**
 * Created by SourabhSadhu on 12-12-2016.
 */

public class Pojo implements java.io.Serializable {

    String number;
    String action;
    int id;
    String dateTime;
    public Pojo(){

    }
    public Pojo(String number, String action, int id, String dateTime) {
        this.number = number;
        this.action = action;
        this.id = id;
        this.dateTime = dateTime;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
