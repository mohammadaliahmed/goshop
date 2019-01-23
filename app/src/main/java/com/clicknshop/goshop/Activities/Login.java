package com.clicknshop.goshop.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clicknshop.goshop.Models.Customer;
import com.clicknshop.goshop.R;
import com.clicknshop.goshop.Utils.CommonUtils;
import com.clicknshop.goshop.Utils.Constants;
import com.clicknshop.goshop.Utils.PrefManager;
import com.clicknshop.goshop.Utils.SharedPrefs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    DatabaseReference mDatabase;
    EditText e_username, e_password, e_phone;
    private PrefManager prefManager;
    ArrayList<String> userlist = new ArrayList<String>();
    String username, password;
    Button login;
    TextView register;
    String productId;
    String takeUserToActivity;
    String phone;

    SignInButton google;
    GoogleApiClient apiClient;
    GoogleSignInAccount account;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
//        e_username = findViewById(R.id.username);
//        e_password = findViewById(R.id.password);
        e_phone = findViewById(R.id.phone);
        Intent i = getIntent();
        takeUserToActivity = i.getStringExtra("takeUserToActivity");
        productId = i.getStringExtra("productId");
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        apiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("customers");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userlist.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        google = findViewById(R.id.google);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin();
            }
        });


        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
                finish();

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
    }

    private void signin() {
        Intent i = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(i, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(googleSignInResult);
        }
    }

    private void handleResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            account = googleSignInResult.getSignInAccount();
            loginUser(account.getEmail().replace("@", "").replace(".", ""));

        }
    }

    private void loginUser(final String userid) {
        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Customer user = dataSnapshot.getValue(Customer.class);
                    if (user != null) {
                        e_phone.setText("" + user.getPhone());

//                        if (user.isVerified()) {
                        SharedPrefs.setUsername(userid);
                        SharedPrefs.setName(user.getName());
//                                    SharedPrefs.setCity(user.getCity());
                        SharedPrefs.setPhone(user.getPhone());
                        SharedPrefs.setIsLoggedIn("yes");
                        CommonUtils.showToast("Successfully Signed In");
                        launchHomeScreen();
//                        } else {
//                            SharedPrefs.setUsername(user.getPhone());
//                            SharedPrefs.setName(user.getName());
////                                    SharedPrefs.setCity(user.getCity());
//                            SharedPrefs.setPhone(user.getPhone());
//                            SharedPrefs.setIsLoggedIn("yes");
//                            CommonUtils.showToast("Not verified");
//                            CommonUtils.sendMessage(phone, user.getCode() + " is your verification code\n\nGo Shop");
//                            Intent i = new Intent(Login.this, PhoneVerification.class);
//                            i.putExtra("phone", phone);
//                            i.putExtra("username", username);
//                            startActivity(i);
//
//
//                        }
                    }
                } else {
                    CommonUtils.showToast("Account does not exist\nPlease register");
                    Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Intent i = new Intent(Login.this, Register.class);
                            startActivity(i);
                            finish();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void userLogin() {

        if (e_phone.getText().toString().length() == 0) {
            e_phone.setError("Please enter phone number");
        }
// else if (e_password.getText().toString().length() == 0) {
//            e_password.setError("Please enter your password");
//        }
        else {
//            username = e_username.getText().toString();
//            password = e_password.getText().toString();
            phone = e_phone.getText().toString();
            if (userlist.contains(phone)) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            Customer user = dataSnapshot.child("" + phone).getValue(Customer.class);
                            if (user != null) {

                                if (user.isVerified()) {
                                    SharedPrefs.setUsername(user.getPhone());
                                    SharedPrefs.setName(user.getName());
//                                    SharedPrefs.setCity(user.getCity());
                                    SharedPrefs.setPhone(user.getPhone());
                                    SharedPrefs.setIsLoggedIn("yes");
                                    CommonUtils.showToast("Successfully Signed In");
                                    launchHomeScreen();
                                } else {
                                    SharedPrefs.setUsername(user.getPhone());
                                    SharedPrefs.setName(user.getName());
//                                    SharedPrefs.setCity(user.getCity());
                                    SharedPrefs.setPhone(user.getPhone());
                                    SharedPrefs.setIsLoggedIn("yes");
                                    CommonUtils.showToast("Not verified");
                                    CommonUtils.sendMessage(phone, user.getCode() + " is your verification code\n\nGo Shop");
                                    Intent i = new Intent(Login.this, PhoneVerification.class);
                                    i.putExtra("phone", phone);
                                    i.putExtra("username", username);
                                    startActivity(i);


                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                CommonUtils.showToast("Username does not exist\nPlease Sign up");

            }
        }

    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        if (takeUserToActivity == null) {
            startActivity(new Intent(Login.this, MainActivity.class));
        } else if (takeUserToActivity.equalsIgnoreCase(Constants.HOME_ACTIVITY)) {
            startActivity(new Intent(Login.this, ListOfProducts.class));

        } else if (takeUserToActivity.equalsIgnoreCase(Constants.CART_ACTIVITY)) {
            startActivity(new Intent(Login.this, Cart.class));
        } else if (takeUserToActivity.equalsIgnoreCase(Constants.MY_ORDERS_ACTIVITY)) {
            startActivity(new Intent(Login.this, MyOrders.class));
        } else if (takeUserToActivity.equalsIgnoreCase(Constants.LIVE_CHAT)) {
            startActivity(new Intent(Login.this, LiveChat.class));
        } else if (takeUserToActivity.equalsIgnoreCase(Constants.PRODUCT_DETAIL_ACTIVITY)) {
            Intent i = new Intent(Login.this, ProductDescription.class);
            i.putExtra("productId", productId);
            startActivity(i);

        }

        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
