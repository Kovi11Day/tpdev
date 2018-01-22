package com.test.kovilapauvaday.prototype_connect.users_amies_profile;

/**
 * Created by kovilapauvaday on 17/12/2017.
 */

public class User {
    protected String id;
    protected String name;
    protected String firebaseId;
    public boolean selected;


    public User(){

    }

    public User(String name, String id){
        this.id = id; //numero
        this.name = name; //pseudo
        this.firebaseId = "";
        this.selected = false;
    }

    public User(String name, String id, String firebaseId){
        this.id = id; //numero
        this.name = name; //pseudo
        this.firebaseId = firebaseId;
        this.selected = false;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    public String getFirebaseId() {return this.firebaseId;}

    public void setFirebaseId(String firebaseId) {this.firebaseId = firebaseId;}
    public boolean isChecked(){return selected;}

    public void switchCheckbox(){
        this.selected = !this.selected;
    }
    @Override
    public String toString(){
        return "id = " + id + ", " + "name= " + name +"\n";
    }

    //----------------------
    public String getPseudo() {
        return name;
    }

    public void setPseudo(String pseudo) {
        this.name = pseudo;
    }

    public String getNumero() {
        return id;
    }

    public void setNumero(String numero) {
        this.id = numero;
    }
}
