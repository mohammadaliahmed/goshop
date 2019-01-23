package com.clicknshop.goshop.Interface;

import com.clicknshop.goshop.Models.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserClient {


    @GET("/web_distributor/api/sms.php")
    Call<Example> createTask(
            @Query("username") String username,
            @Query("password") String password,
            @Query("mobile") String mobile,
            @Query("sender") String sender,
            @Query("message") String message

    );


}
