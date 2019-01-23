package com.clicknshop.goshop.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clicknshop.goshop.Models.Customer;
import com.clicknshop.goshop.R;
import com.clicknshop.goshop.Utils.CommonUtils;
import com.clicknshop.goshop.Utils.PrefManager;
import com.clicknshop.goshop.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PhoneVerification extends AppCompatActivity {
    EditText codeEntered;
    TextView resend, phone;
    DatabaseReference mDatabase;
    String phoneNumber, username;
    Button confirm,signout;
    Customer customer;
    private long randomCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        this.setTitle("Phone Verification");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        phoneNumber = getIntent().getStringExtra("phone");
        username = getIntent().getStringExtra("username");

        resend = findViewById(R.id.resend);
        phone = findViewById(R.id.phone);
        codeEntered = findViewById(R.id.codeEntered);
        confirm = findViewById(R.id.confirm);
        signout = findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefManager prefManager = new PrefManager(PhoneVerification.this);
                prefManager.setFirstTimeLaunch(true);
                SharedPrefs.logout();
                Intent i = new Intent(PhoneVerification.this, Login.class);
                startActivity(i);
                finish();
            }
        });


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomCode = CommonUtils.randomCode();
                CommonUtils.sendMessage(phoneNumber, randomCode + " is your verification code\n\nGo Shop");
                updateCodeInDB(randomCode);
            }
        });


        mDatabase.child("customers").child(phoneNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    customer = dataSnapshot.getValue(Customer.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        phone.setText(phoneNumber);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codeEntered.getText().length() == 0) {
                    codeEntered.setError("Enter Code");
                } else {

                    if (customer.getCode() == Long.parseLong(codeEntered.getText().toString())) {
                        updateUserStatus();

                    } else {
                        CommonUtils.showToast("Wrong code entered\nPlease try again");
                    }
                }
            }
        });


    }

    private void updateCodeInDB(long randomCode) {
        mDatabase.child("customers").child(phoneNumber).child("code").setValue(randomCode).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.showToast("Code Sent");
            }
        });
    }

    private void updateUserStatus() {
        mDatabase.child("customers").child(phoneNumber).child("verified").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                PrefManager pr = new PrefManager(PhoneVerification.this);
                pr.setFirstTimeLaunch(false);
                SharedPrefs.setIsLoggedIn("yes");
                CommonUtils.showToast("Verified");
                Intent i = new Intent(PhoneVerification.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}
