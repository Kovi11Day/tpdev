package com.test.kovilapauvaday.prototype_connect.model;

import android.content.Context;

import com.facebook.Profile;
import com.test.kovilapauvaday.prototype_connect.LocalisationGPS;

import java.util.ArrayList;

/**
 * Created by kovilapauvaday on 12/12/2017.
 */

public class GlobalDataSingleton {

    private ArrayList<User> friends;
    LocalisationGPS localisationGPS;

    private GlobalDataSingleton(){

        friends = new ArrayList<>();
        this.localisationGPS = null;
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

    public ArrayList<User> getSelectedFriends(){
        ArrayList<User> selectedUsers = new ArrayList<>();
        for(User user: friends){
            if(user.selected)
                selectedUsers.add(user);
        }
        return selectedUsers;
    }

    public void unselectAll(){
        for(User user: friends){
            user.selected = false;
        }
    }

    public LocalisationGPS getLocalisationGPS(Context context){
        if (this.localisationGPS == null){
            this.localisationGPS = new LocalisationGPS(context);
        }
        return this.localisationGPS;
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
