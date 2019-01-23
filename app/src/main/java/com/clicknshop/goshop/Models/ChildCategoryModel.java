package com.clicknshop.goshop.Models;

public class ChildCategoryModel {
    String childCategory;
    int position;

    public ChildCategoryModel(String childCategory, int position) {
        this.childCategory = childCategory;
        this.position = position;
    }

    public ChildCategoryModel() {
    }

    public String getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(String childCategory) {
        this.childCategory = childCategory;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
