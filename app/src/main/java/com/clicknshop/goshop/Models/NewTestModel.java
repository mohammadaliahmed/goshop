package com.clicknshop.goshop.Models;

public class NewTestModel {
    String categories;
    int position;

    public NewTestModel(String categories, int position) {
        this.categories = categories;
        this.position = position;
    }

    public NewTestModel() {
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
