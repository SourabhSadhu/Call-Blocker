package com.model;

/**
 * Created by SourabhSadhu on 12-12-2016.
 */

public class Pojo implements java.io.Serializable {

    String number;
    String action;
    int id;
    public Pojo(){

    }
    public Pojo(String number, String action, int id) {
        this.number = number;
        this.action = action;
        this.id = id;
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

}
