package com.clicknshop.goshop.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clicknshop.goshop.Adapters.CartAdapter;
import com.clicknshop.goshop.Adapters.MainSliderAdapter;
import com.clicknshop.goshop.BannerModel;
import com.clicknshop.goshop.Interface.AddToCartInterface;
import com.clicknshop.goshop.Models.Product;
import com.clicknshop.goshop.Models.ProductCountModel;
import com.clicknshop.goshop.R;
import com.clicknshop.goshop.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    public static ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    DatabaseReference mDatabase;
    CartAdapter adapter;
    TextView subtotal, totalAmount, grandTotal;
    long total;
    int items;
    public static long grandTotalAmount;
    RelativeLayout checkout, wholeLayout, noItemInCart;
    Button startShopping;
    public static long deliveryChargess;
    public static long productTotal;
    TextView deliveryCharges;
    MainSliderAdapter mViewPagerAdapter;
    ViewPager viewPager;
    int currentPic = MainActivity.currentPic;
    ArrayList<BannerModel> pics = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        this.setTitle("My Cart");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        subtotal = findViewById(R.id.subtotal);
        totalAmount = findViewById(R.id.totalAmount);
        grandTotal = findViewById(R.id.totalPrice);
        checkout = findViewById(R.id.checkout);
        startShopping = findViewById(R.id.startShopping);
        wholeLayout = findViewById(R.id.wholeLayout);
        noItemInCart = findViewById(R.id.noItemInCart);
        deliveryCharges = findViewById(R.id.deliveryCharges);

        startShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Cart.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Cart.this, Checkout.class);
                i.putExtra("grandTotal", grandTotalAmount);
                startActivity(i);
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference();
        getDeliveryChargesFromDB();
        initViewPager();


        recyclerView = findViewById(R.id.recycler);
        calculateTotal();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CartAdapter(Cart.this, userCartProductList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                mDatabase.child("customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).setValue(new ProductCountModel(product, quantity, System.currentTimeMillis()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                calculateTotal();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void deletedFromCart(final Product product, final int position) {
                userCartProductList.remove(position);
                if (userCartProductList.isEmpty()) {
                    wholeLayout.setVisibility(View.GONE);
                    noItemInCart.setVisibility(View.VISIBLE);

                }

                mDatabase.child("customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        getDeliveryChargesFromDB();
                        calculateTotal();

//                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void quantityUpdate(Product product, final int quantity, int position) {
                mDatabase.child("customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).child("quantity").setValue(quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        calculateTotal();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });
        recyclerView.setAdapter(adapter);
        getUserCartProductsFromDB();


    }

    private void getDeliveryChargesFromDB() {
        mDatabase.child("Settings").child("DeliveryCharges").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    deliveryChargess = dataSnapshot.getValue(Long.class);
                    deliveryCharges.setText("TSh. " + deliveryChargess);
                    calculateTotal();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void calculateTotal() {

        total = 0;
        items = 0;
        grandTotalAmount = 0;
        mDatabase.child("customers").child(SharedPrefs.getUsername()).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    noItemInCart.setVisibility(View.GONE);
                    wholeLayout.setVisibility(View.VISIBLE);
                    total = 0;
                    items = 0;
                    grandTotalAmount = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel model = snapshot.getValue(ProductCountModel.class);
                        if (model != null) {
                            total = total + (model.getQuantity() * model.getProduct().getPrice());
                            subtotal.setText("TSh. " + total);
//                            if (deliveryChargess != 0) {
                            productTotal = total;
                            grandTotalAmount = total + deliveryChargess;
                            totalAmount.setText("TSh. " + grandTotalAmount);
                            grandTotal.setText("TSh. " + grandTotalAmount);
//                            }


                        }
                    }
                } else {
                    total = 0;
                    items = 0;
                    grandTotalAmount = 0;
                    noItemInCart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserCartProductsFromDB() {
//        mDatabase.child("Settings").child("DeliveryCharges").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    deliveryChargess = dataSnapshot.getValue(Long.class);
//                    deliveryCharges.setText("TSh. " + deliveryChargess);
//                    calculateTotal();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        mDatabase.child("customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userCartProductList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductCountModel product = snapshot.getValue(ProductCountModel.class);
                        if (product != null) {
                            userCartProductList.add(product);
                            Collections.sort(userCartProductList, new Comparator<ProductCountModel>() {
                                @Override
                                public int compare(ProductCountModel listData, ProductCountModel t1) {
                                    Long ob1 = listData.getTime();
                                    Long ob2 = t1.getTime();

                                    return ob2.compareTo(ob1);

                                }
                            });
                            adapter.notifyDataSetChanged();
                        }
                    }
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
