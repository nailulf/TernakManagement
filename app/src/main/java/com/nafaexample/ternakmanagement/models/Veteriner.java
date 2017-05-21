package com.nafaexample.ternakmanagement.models;

/**
 * Created by Nailul on 4/18/2017.
 */

public class Veteriner {
    public String name;
    public String adress;
    public String phone;
    public String image;

    public Veteriner(){}

    public Veteriner(String name, String adress, String phone, String image){
        this.name = name;
        this.phone = phone;
        this.adress = adress;
        this.image = image;
    }
}
