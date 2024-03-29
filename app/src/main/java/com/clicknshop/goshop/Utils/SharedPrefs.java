package com.clicknshop.goshop.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.clicknshop.goshop.ApplicationClass;


/**
 * Created by AliAh on 20/02/2018.
 */

public class SharedPrefs {


    private SharedPrefs() {

    }

    public static String getUsername() {
        return preferenceGetter("username");
    }

    public static void setCartCount(String count) {
        preferenceSetter("cartCount", count);
    }

    public static String getCartCount() {
        return preferenceGetter("cartCount");
    }

    public static void setUsername(String username) {
        preferenceSetter("username", username);
    }


    public static void setName(String value) {

        preferenceSetter("name", value);
    }

    public static String getName() {
        return preferenceGetter("name");
    }

    public static void setPhone(String value) {

        preferenceSetter("phone", value);
    }

    public static String getPhone() {
        return preferenceGetter("phone");
    }


    public static void setCity(String value) {

        preferenceSetter("city", value);
    }

    public static String getCity() {
        return preferenceGetter("city");
    }


    public static void setIsLoggedIn(String value) {

        preferenceSetter("isLoggedIn", value);
    }

    public static String getIsLoggedIn() {
        return preferenceGetter("isLoggedIn");
    }


    public static void setFcmKey(String fcmKey) {
        preferenceSetter("fcmKey", fcmKey);
    }

    public static String getFcmKey() {
        return preferenceGetter("fcmKey");
    }

   public static void setAdminPhone(String fcmKey) {
        preferenceSetter("adminPhone", fcmKey);
    }

    public static String getAdminPhone() {
        return preferenceGetter("adminPhone");
    }


    public static void preferenceSetter(String key, String value) {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String preferenceGetter(String key) {
        SharedPreferences pref;
        String value = "";
        pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        value = pref.getString(key, "");
        return value;
    }
    public static void logout(){
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
