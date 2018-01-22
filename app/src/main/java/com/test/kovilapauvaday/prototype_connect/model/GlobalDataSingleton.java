package com.test.kovilapauvaday.prototype_connect.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.test.kovilapauvaday.prototype_connect.LocalisationGPS;
import com.test.kovilapauvaday.prototype_connect.users_amies_profile.ProfileActivity;
import com.test.kovilapauvaday.prototype_connect.users_amies_profile.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by kovilapauvaday on 12/12/2017.
 */

public class GlobalDataSingleton {

    private ArrayList<User> friends;
    private HashSet<User> firebaseUser;

    LocalisationGPS localisationGPS;

    private GlobalDataSingleton(){

        friends = new ArrayList<>();
        firebaseUser = new HashSet<>();

        this.localisationGPS = null;
    }
    private static GlobalDataSingleton instance = null;

    public static GlobalDataSingleton getInstance(){
        if(instance == null)
            instance = new GlobalDataSingleton();
        return instance;
    }
    public User findUser(String firebaseId){
        for(User u: this.firebaseUser){
            if (u.getFirebaseId().equals(firebaseId)){
                return u;
            }
        }
        return null;
    }
    public void addFriend(String name, String id){
        this.friends.add(new User(name, id));
    }

    //if facebook friend then add to amies in firebase database
    public void addFireBaseUser(User user){
        //if(!this.firebaseUser.contains(((User)user))) {
            this.firebaseUser.add(user);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //check if returns true
        //User me = this.findUser(id);
        //String mePseudo = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        //String meNumero = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        String mePseudo = Profile.getCurrentProfile().getName();
        String meNumero = Profile.getCurrentProfile().getId();
        Log.v("&&pseudo", mePseudo);
        Log.v("&&numero",meNumero);

        for(User f: this.getFriends()){
                if (f.getId().equals(user.getId())){ //if facebook friend is user of app
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Amies").child(id);
                    DatabaseReference usersdb = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
                    if(!id.equals(user.getFirebaseId())) {
                        Map friendsMap = new HashMap();
                        friendsMap.put("Amies/" + id + "/" + user.getFirebaseId() + "/pseudo", f.getPseudo());
                        friendsMap.put("Amies/" + id + "/" + user.getFirebaseId() + "/numero", f.getNumero());
                        friendsMap.put("Amies/" + user.getFirebaseId() + "/" + id + "/pseudo", mePseudo);
                        friendsMap.put("Amies/" + user.getFirebaseId() + "/" + id + "/numero", meNumero);
                        Log.v("&&", user.getFirebaseId() + "==" + id);

                        FirebaseDatabase.getInstance().getReference().updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    Log.v("&&",   "yessss" );

                                } else {

                                    Log.v("&&", "nooooo:" + databaseError.getMessage());

                                }
                            }
                        });
                    }
                }

                    /*HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("numero", f.getNumero());
                    userMap.put("pseudo", f.getPseudo());
                    database.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                            }
                        }
                    });*/
                }
            //}

        }


    public ArrayList<User> getFriends(){
        return this.friends;
    }

    public HashSet<User> getFirebaseUser(){
        return this.firebaseUser;
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
