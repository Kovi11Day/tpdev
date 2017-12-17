package com.test.kovilapauvaday.prototype_connect.model;

import com.facebook.Profile;

import java.util.ArrayList;

/**
 * Created by kovilapauvaday on 12/12/2017.
 */

public class GlobalDataSingleton {

    //private Profile profile;
    //private String str= "initial";
    private ArrayList<User> friends;

    private GlobalDataSingleton(){
        friends = new ArrayList<>();
    }
    private static GlobalDataSingleton instance = null;

    public static GlobalDataSingleton getInstance(){
        if(instance == null)
            instance = new GlobalDataSingleton();
        return instance;
    }

    public void addFriend(String name, String id){
        this.friends.add(new User(name, id));
    }

    public ArrayList<User> getFriends(){
        return this.friends;
    }
  /*  public void setProfile(Profile profile){
        this.profile = profile;
    }

    public Profile getProfile (){
        return this.profile;
    }

    public void setStr(String str){
        this.str = str;
    }

    public String getStr (){
        return this.str;
    }*/
}
