package com.call.block.group.model;

/**
 * Created by SourabhSadhu on 12-12-2016.
 */

public class Pojo implements java.io.Serializable {

    String name;
    String number;
    String action;
    int id;
    String dateTime;
    String block_action;

    public Pojo(){

    }
    public Pojo(String name,String number, String action, int id, String dateTime, String block_action) {
        this.name = name;
        this.number = number;
        this.action = action;
        this.id = id;
        this.dateTime = dateTime;
        this.block_action = block_action;
    }

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
    public String getBlock_action() {
        return block_action;
    }
    public void setBlock_action(String block_action) {
        this.block_action = block_action;
    }
}
