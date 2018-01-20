package com.test.kovilapauvaday.prototype_connect.model;

/**
 * Created by kovilapauvaday on 17/12/2017.
 */

public class User {
    protected String id;
    protected String name;
    protected boolean selected;

    public User(){

    }

    public User(String name, String id){
        this.id = id; //numero
        this.name = name; //pseudo
        this.selected = false;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

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
