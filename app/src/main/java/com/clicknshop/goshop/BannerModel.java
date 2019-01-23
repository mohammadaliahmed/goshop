package com.clicknshop.goshop;

public class BannerModel {
    String bannerId, bannerUrl, brand, position="1";

    public BannerModel(String bannerId, String bannerUrl, String brand, String position) {
        this.bannerId = bannerId;
        this.bannerUrl = bannerUrl;
        this.brand = brand;
        this.position = position;
    }

    public BannerModel() {
    }

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
