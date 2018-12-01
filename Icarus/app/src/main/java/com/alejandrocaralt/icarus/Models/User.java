package com.alejandrocaralt.icarus.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @Expose
    @SerializedName("idUser")
    private String idUser ;


    @Expose
    @SerializedName("email")
    private String email ;

    public User() {}

    public User(String idUser, String email) {
        this.idUser = idUser;
        this.email = email;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
