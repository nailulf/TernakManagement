package com.nafaexample.ternakmanagement.models;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nailul on 4/24/2017.
 */

public class User {

    public String user;
    public String email;
    public String phone;
    public String farm;
    public String uid;
    public String image;
    public int numCattle;

    public User() {
    }

    public User(String user,  String email, String phone, String farm,  String uid, String image, int numCattle) {

        this.user = user;
        this.phone = phone;
        this.email = email;
        this.farm = farm;
        this.uid = uid;
        this.image = image;
        this.numCattle = numCattle;
    }

    public User(String user, String farm, String phone, String email,  String image) {

        this.user = user;
        this.phone = phone;
        this.farm = farm;
        this.email = email;
        this.image = image;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("uid", uid);
        result.put("phone", phone);
        result.put("email",email);
        result.put("farm",farm);
        result.put("image", image);
        result.put("numCattle", numCattle);

        return result;
    }
    @Exclude
    public Map<String, Object> toMapUp() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("phone", phone);
        result.put("email",email);
        result.put("farm",farm);
        result.put("image", image);;

        return result;
    }
    /*public String getUser() {

        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public long getNumCattle() {
        return numCattle;
    }

    public void setNumCattle(long numCattle) {
        this.numCattle = numCattle;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }*/
}
