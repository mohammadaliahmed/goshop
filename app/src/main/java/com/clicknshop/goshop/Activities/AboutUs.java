package com.clicknshop.goshop.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.clicknshop.goshop.R;
import com.google.firebase.database.DatabaseReference;

public class AboutUs extends AppCompatActivity {
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }
}
