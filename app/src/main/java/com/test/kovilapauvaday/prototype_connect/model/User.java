package com.test.kovilapauvaday.prototype_connect.model;

/**
 * Created by kovilapauvaday on 17/12/2017.
 */

public class User {
    protected final String id;
    protected String name;

    public User(String name, String id){
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    @Override
    public String toString(){
        return "id = " + id + ", " + "name= " + name +"\n";
    }
}
