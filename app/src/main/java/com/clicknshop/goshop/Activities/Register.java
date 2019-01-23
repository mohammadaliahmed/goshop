package com.clicknshop.goshop.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clicknshop.goshop.Models.ChatModel;
import com.clicknshop.goshop.Models.Customer;
import com.clicknshop.goshop.R;
import com.clicknshop.goshop.Utils.CommonUtils;
import com.clicknshop.goshop.Utils.PrefManager;
import com.clicknshop.goshop.Utils.SharedPrefs;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    Button signup;
    TextView login;
    DatabaseReference mDatabase;
    private PrefManager prefManager;
    ArrayList<String> userslist = new ArrayList<String>();
    EditText e_fullname, e_username, e_email, e_password, e_phone, e_city, e_address;
    String fullname, username, email, password, phone, address, city;
    long time;
    private long randomCode;
    SignInButton google;
    GoogleApiClient apiClient;
    GoogleSignInAccount account;
    String userid;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        randomCode = CommonUtils.randomCode();
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        apiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();


        mDatabase.child("customers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userslist.add(dataSnapshot.getKey());
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

        e_fullname = findViewById(R.id.name);
//        e_username = findViewById(R.id.username);
        e_email = findViewById(R.id.email);
        e_password = findViewById(R.id.password);
        e_phone = findViewById(R.id.phone);
        e_address = findViewById(R.id.address);
        e_city = findViewById(R.id.city);

        signup = findViewById(R.id.signup);
        login = findViewById(R.id.signin);
        google = findViewById(R.id.google);



        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (e_fullname.getText().toString().length() == 0) {
                    e_fullname.setError("Cannot be null");
                }
//                else if (e_username.getText().toString().length() == 0) {
//                    e_username.setError("Cannot be null");
//                }
//                else if (e_email.getText().toString().length() == 0) {
//                    e_email.setError("Cannot be null");
//                }
//                else if (e_password.getText().toString().length() == 0) {
//                    e_password.setError("Cannot be null");
//                }
                else if (e_phone.getText().toString().length() == 0) {
                    e_phone.setError("Cannot be null");
                } else if (e_address.getText().toString().length() == 0) {
                    e_address.setError("Cannot be null");
                }
//                else if (e_city.getText().toString().length() == 0) {
//                    e_city.setError("Cannot be null");
//                }
                else {
                    fullname = e_fullname.getText().toString();
//                    username = e_username.getText().toString();
//                    email = e_email.getText().toString();
//                    password = e_password.getText().toString();
                    phone = e_phone.getText().toString();
                    address = e_address.getText().toString();
//                    city = e_city.getText().toString();

                    if (userslist.contains("" + userid)) {
                        Toast.makeText(Register.this, "Username is already taken\nPlease choose another", Toast.LENGTH_SHORT).show();
                    } else {
//                        int randomPIN = (int) (Math.random() * 900000) + 100000;
                        time = System.currentTimeMillis();
//                        final String userId = "" + time;
                        if (account == null) {
                            userid = phone;
                        } else {
                            userid = account.getEmail().replace("@", "").replace(".", "");
                        }
                        mDatabase.child("customers")
                                .child(userid)
                                .setValue(new Customer(userid, fullname, phone, email + " ",
                                        password + " ", "" + phone, address, city + " ", SharedPrefs.getFcmKey(), time,
                                        randomCode, true))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(Register.this, "Thankyou for registering", Toast.LENGTH_SHORT).show();
//                                        CommonUtils.sendMessage(phone, randomCode + " is your verification code\n\nGo Shop");
                                        SharedPrefs.setUsername(userid);
                                        SharedPrefs.setPhone(phone);
                                        SharedPrefs.setName(fullname);
//                                        SharedPrefs.setCity(city);

//                                        startChatWithAdmin(username);
                                        launchHomeScreen();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, "There was some error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
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
            e_fullname.setText(account.getDisplayName());
            if (userslist.contains(account.getEmail().replace("@", "").replace(".", ""))) {
                loginUser(account.getEmail().replace("@", "").replace(".", ""));
                Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
            } else {
                Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
            }
//            CommonUtils.showToast(account.getDisplayName() +"    "+ account.getEmail()+account.get);
        }
    }

    private void loginUser(final String userid) {
        mDatabase.child("customers").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            Intent i = new Intent(Register.this, Register.class);
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

    private void launchHomeScreen() {
//        prefManager.setFirstTimeLaunch(false);

        Intent i = new Intent(Register.this, MainActivity.class);
        i.putExtra("phone", phone);
        i.putExtra("username", phone);
        startActivity(i);

        finish();
    }

    private void startChatWithAdmin(String user) {
//        final String key = mDatabase.push().getKey();
//        mDatabase.child("Chats").child(user).child(key)
//                .setValue(new ChatModel(key, "I just registered on your app", user
//                        , System.currentTimeMillis(), "sent", user, ""));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
