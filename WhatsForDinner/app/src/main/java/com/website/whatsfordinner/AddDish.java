package com.website.whatsfordinner;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by jayashreemadhanraj on 9/11/16.
 */
public class AddDish {

    private byte[] photoURI;
    private String dishName;
    private ArrayList<String> items;
    private String instruction;
    private ArrayList<Integer> count;

    public AddDish(String dishName, byte[] iURI, ArrayList<String> items, ArrayList<Integer> count, String instruction){
        this.items = new ArrayList<String>();
        this.count = new ArrayList<Integer>();
       this.items = items;
        this.dishName = dishName;
        this.photoURI = iURI;
        this.instruction = instruction;
        this.count = count;
    }

    public String getdishName(){
        return dishName;
    }

    public ArrayList<String> getItems(){
        return items;
    }

    public ArrayList<Integer> getCount() {
        return count;
    }

    public String getInstruction(){
        return instruction;
    }

    public byte[] getPhotoURI(){

        return photoURI;
    }

}
