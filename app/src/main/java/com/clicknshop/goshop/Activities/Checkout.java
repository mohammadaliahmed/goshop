package com.clicknshop.goshop.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.clicknshop.goshop.Adapters.MainSliderAdapter;
import com.clicknshop.goshop.BannerModel;
import com.clicknshop.goshop.Interface.NotificationObserver;
import com.clicknshop.goshop.Models.Customer;
import com.clicknshop.goshop.Models.OrderModel;
import com.clicknshop.goshop.Models.ProductCountModel;
import com.clicknshop.goshop.R;
import com.clicknshop.goshop.Utils.CommonUtils;
import com.clicknshop.goshop.Utils.NotificationAsync;
import com.clicknshop.goshop.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Checkout extends AppCompatActivity implements NotificationObserver {
    long grandTotal;
    RelativeLayout placeOrder, wholeLayout, progress;
    DatabaseReference mDatabase;
    TextView address, totalPrice;
    ImageView editAddress;
    EditText instructions;
    Customer customer;
    long orderId = 1;
    String adminFcmKey, textMessageToSend, admninNumber;
    private double lat, lon;
    private String returnString;
    Spinner spinner1, spinner2;
    String timeSelected, daySelected;

    MainSliderAdapter mViewPagerAdapter;
    ViewPager viewPager;
    int currentPic = MainActivity.currentPic;
    ArrayList<BannerModel> pics = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        this.setTitle("Checkout");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        Intent i = getIntent();
        grandTotal = i.getLongExtra("grandTotal", 0);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        instructions = findViewById(R.id.instructions);
        placeOrder = findViewById(R.id.placeOrder);
        address = findViewById(R.id.address);
        totalPrice = findViewById(R.id.totalPrice);
        editAddress = findViewById(R.id.editAddress);
        progress = findViewById(R.id.progress);
        wholeLayout = findViewById(R.id.wholeLayout);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);


        getOrderCountFromDB();
        initViewPager();

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Checkout.this, MapsActivity.class);
                startActivityForResult(i, 1);
            }
        });

        totalPrice.setText("TSh. " + grandTotal);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    customer = dataSnapshot.child("customers").child(SharedPrefs.getUsername()).getValue(Customer.class);
                    if (customer != null) {
                        address.setText(customer.getAddress());
                    }
                    adminFcmKey = dataSnapshot.child("Admin").child("admin").child("fcmKey").getValue(String.class);
                    admninNumber = dataSnapshot.child("Settings").child("AdminNumber").getValue(String.class);
                    SharedPrefs.setAdminPhone(admninNumber);
                    textMessageToSend = dataSnapshot.child("Settings").child("TextMessage").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                if (customer != null) {
                    if (customer.getAddress().equalsIgnoreCase("")) {
                        CommonUtils.showToast("Please select your delivery location");
                        Intent i = new Intent(Checkout.this, MapsActivity.class);
                        startActivityForResult(i, 1);
                    } else {
                        mDatabase.child("Orders").child("" + orderId)
                                .setValue(new OrderModel("" + orderId,
                                        customer,
                                        Cart.userCartProductList,
                                        Cart.grandTotalAmount,
                                        System.currentTimeMillis(),
                                        instructions.getText().toString() + " ",
                                        daySelected, timeSelected,
                                        "Pending",
                                        customer.getAddress(), customer.getLat(), customer.getLon(),
                                        Cart.deliveryChargess,
                                        Cart.productTotal

                                )).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDatabase.child("customers").child(SharedPrefs.getUsername()).child("Orders").child("" + orderId).setValue(orderId);
                                String head = "New Order On Go Shop\n"
                                        + "Order Id: " + orderId
                                        + "\nCustomer Details: \nName:" + SharedPrefs.getName() + "\nPhone: " + SharedPrefs.getPhone() +
                                        "\nItems: ";
                                String ab = "";

                                for (ProductCountModel abc : Cart.userCartProductList) {
                                    ab = (abc.getQuantity() + " * " + abc.getProduct().getTitle()) + " " + abc.getProduct().getSubtitle() + "\n" + ab;
                                }
                                final String msg = head + "\n" + ab + "\n\nTotal: TSh " + Cart.grandTotalAmount + "\n\nLocation: https://maps.google.com/?daddr=" + customer.getLat() + "," + customer.getLon();

                                mDatabase.child("customers").child(SharedPrefs.getUsername()).child("cart").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        CommonUtils.sendMessage(admninNumber, msg);
                                        CommonUtils.sendMessage(SharedPrefs.getPhone(), "Dear " + SharedPrefs.getName() + " " + textMessageToSend);

                                        NotificationAsync notificationAsync = new NotificationAsync(Checkout.this);
                                        String NotificationTitle = "New order from " + SharedPrefs.getName();
                                        String NotificationMessage = "Click to view ";
                                        notificationAsync.execute("ali", adminFcmKey, NotificationTitle, NotificationMessage, "Order", "1");

                                        Intent i = new Intent(Checkout.this, OrderPlaced.class);
                                        i.putExtra("total", "" + Cart.grandTotalAmount);
                                        i.putExtra("time", timeSelected);
                                        i.putExtra("day", daySelected);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }

            }
        });
        final String[] items = new String[]{"Today", "Tomorrow"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                daySelected = items[i];
                setupSpinner2(daySelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void setupSpinner2(String daySelected) {
        long time = System.currentTimeMillis();
        final String[] items2;

        if (daySelected.equalsIgnoreCase("Tomorrow")) {
            items2 = new String[]{"11:00 - 01:00 PM", "03:00 - 05:00 PM", "05:00 - 07:00 PM", "07:00 - 09:00 PM"};
        } else {
            if (CommonUtils.getDayName(time).equalsIgnoreCase("Sun")) {
                if (CommonUtils.getHour(time) >= 8 && CommonUtils.getHour(time) <= 10) {
                    items2 = new String[]{"11:00 - 01:00 PM", "03:00 - 05:00 PM"};
                } else if (CommonUtils.getHour(time) >= 10 && CommonUtils.getHour(time) <= 14) {
                    items2 = new String[]{"03:00 - 05:00 PM"};
                } else {
                    items2 = new String[]{""};
                }
            } else {
                if (CommonUtils.getHour(time) >= 8 && CommonUtils.getHour(time) <= 10) {
                    items2 = new String[]{"11:00 - 01:00 PM", "03:00 - 05:00 PM", "05:00 - 07:00 PM", "07:00 - 09:00 PM"};
                } else if (CommonUtils.getHour(time) >= 10 && CommonUtils.getHour(time) <= 14) {
                    items2 = new String[]{"03:00 - 05:00 PM", "05:00 - 07:00 PM", "07:00 - 09:00 PM"};
                } else if (CommonUtils.getHour(time) >= 14 && CommonUtils.getHour(time) <= 16) {
                    items2 = new String[]{"05:00 - 07:00 PM", "07:00 - 09:00 PM"};
                } else if (CommonUtils.getHour(time) >= 16 && CommonUtils.getHour(time) <= 18) {
                    items2 = new String[]{"07:00 - 09:00 PM"};
                } else {
                    items2 = new String[]{""};
                }

            }
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeSelected = items2[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getOrderCountFromDB() {
        mDatabase.child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    orderId = dataSnapshot.getChildrenCount() + 1;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initViewPager() {

        viewPager = findViewById(R.id.slider);
        mViewPagerAdapter = new MainSliderAdapter(this, pics);
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPic = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Magic here
                if (currentPic >= pics.size()) {
                    currentPic = 0;
                    viewPager.setCurrentItem(currentPic);
                } else {
                    viewPager.setCurrentItem(currentPic);
                    currentPic++;
                }
                MainActivity.currentPic=currentPic;
                new Handler().postDelayed(this, 2000);
            }
        }, 2000); // Millisecond 1000 = 1 sec
        mDatabase.child("Banners").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BannerModel model = snapshot.getValue(BannerModel.class);
                        if (model != null) {
                            pics.add(model);
                        }
                    }
                    mViewPagerAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(currentPic);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                returnString = data.getStringExtra("address");
                lat = data.getDoubleExtra("lat", 0.0);
                lon = data.getDoubleExtra("lon", 0.0);

                address.setText(returnString);
                progress.setVisibility(View.GONE);

                updateDeliveryAddressToDB(returnString, lat, lon);

            }
        } else if (requestCode == 1 && resultCode == 0) {
            progress.setVisibility(View.GONE);
        }

    }

    private void updateDeliveryAddressToDB(String address, double lat, double lon) {
        mDatabase.child("customers").child(SharedPrefs.getUsername()).child("lat").setValue(lat);
        mDatabase.child("customers").child(SharedPrefs.getUsername()).child("lon").setValue(lon);
        mDatabase.child("customers").child(SharedPrefs.getUsername()).child("address").setValue(address)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Delivery Address updated");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
