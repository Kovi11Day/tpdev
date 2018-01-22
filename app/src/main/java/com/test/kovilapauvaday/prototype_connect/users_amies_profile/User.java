package com.test.kovilapauvaday.prototype_connect.users_amies_profile;

/**
 * Created by ARAM on 30/12/2017.
 */

public class User {
    public String pseudo;
    public String numero;


    public User(){

    }

    public User(String psudo, String numero) {
        this.pseudo = pseudo;
        this.numero = numero;;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

}
