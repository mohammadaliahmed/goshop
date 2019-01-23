package com.clicknshop.goshop.Models;

import java.util.ArrayList;

public class NewCategoryModel {


    int position;
    String name;
    ArrayList<SubCategoryModel> model;

    public NewCategoryModel(int position, String name, ArrayList<SubCategoryModel> model) {
        this.position = position;
        this.name = name;
        this.model = model;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SubCategoryModel> getModel() {
        return model;
    }

    public void setModel(ArrayList<SubCategoryModel> model) {
        this.model = model;
    }
}
