package com.clicknshop.goshop.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.clicknshop.goshop.Adapters.FragmentAdapter;
import com.clicknshop.goshop.Adapters.MainSliderAdapter;
import com.clicknshop.goshop.BannerModel;
import com.clicknshop.goshop.Models.ChildCategoryModel;
import com.clicknshop.goshop.Models.SubCategoryModel;
import com.clicknshop.goshop.R;
import com.clicknshop.goshop.Utils.CommonUtils;
import com.clicknshop.goshop.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListOfProducts extends AppCompatActivity {
    public static TextView textCartItemCount;
    DatabaseReference mDatabase;
    ArrayList<ChildCategoryModel> categoryList = new ArrayList<>();
    long cartItemCountFromDb;
    int pos;
    String category;
    int flag;

    boolean tabScrollable = false;
    MainSliderAdapter mViewPagerAdapter;
    ViewPager viewPager;
    int currentPic = MainActivity.currentPic;
    ArrayList<BannerModel> pics = new ArrayList<>();
    FragmentAdapter adapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_products);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent i = getIntent();
//        pos = i.getIntExtra("position", 0);
        category = i.getStringExtra("category");
//        flag = i.getIntExtra("flag", 0);

//        this.setTitle(category);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getCategoriesFromDB();
        initViewPager();

        ViewPager viewPager = findViewById(R.id.viewpager);
//        if (category != null) {
//            if (category.equalsIgnoreCase("Oil & Ghee")) {
//                categoryList.add("Cooking Oil");
//                categoryList.add("Olive Oil");
//                categoryList.add("Ghee");
//                categoryList.add("Desi Ghee");
//                categoryList.add("Sunflower Oil");
//                categoryList.add("Corn Oil");
//                categoryList.add("Other Edible Oil");
//                categoryList.add("Canola Oil");
//
//            } else if (category.equalsIgnoreCase("Spices, Salt & Sugar")) {
//                categoryList.add("Herbs & Spices");
//                categoryList.add("Salt");
//                categoryList.add("Sugar");
//                categoryList.add("National Masala");
//                categoryList.add("Shan Masala");
//                categoryList.add("Seasoning Cubes");
//                categoryList.add("Vinegar");
//            } else if (category.equalsIgnoreCase("Daalain, Rice & Flour")) {
//                categoryList.add("Daalain");
//                categoryList.add("Rice");
//                categoryList.add("Flour");
//                categoryList.add("Dry Fruit");
//                categoryList.add("Other");
//
//            } else if (category.equalsIgnoreCase("Sauces, Olives & Pickles")) {
//                categoryList.add("Ketchup");
//                categoryList.add("Chilli Sauce");
//                categoryList.add("Mayonise");
//                categoryList.add("Olives");
//                categoryList.add("Pickles");
//
//            } else if (category.equalsIgnoreCase("Jam, Honey & Spread")) {
//                categoryList.add("Jam");
//                categoryList.add("Honey");
//                categoryList.add("Spread");
//                categoryList.add("Syrup");
//
//            } else if (category.equalsIgnoreCase("Baking & Desert")) {
//                categoryList.add("Baking Mix");
//                categoryList.add("Jelly");
//                categoryList.add("Laziza Deserts");
//                categoryList.add("Other");
//            }
////            else if (category.equalsIgnoreCase("Women Care")) {
////                categoryList.add("W Body Spray");
////                categoryList.add("W Roll on");
////                categoryList.add("Pads");
////                categoryList.add("Hair Remover");
////                categoryList.add("Nail Polish Remover");
////
////            }
////            else if (category.equalsIgnoreCase("Men Care")) {
////                categoryList.add("M Roll on");
////                categoryList.add("Body Spray");
////                categoryList.add("Razors");
////                categoryList.add("Shaving Foams");
////                categoryList.add("After Shave");
////
////            }
//            else if (category.equalsIgnoreCase("Hair Care")) {
//                categoryList.add("Hair Color");
//                categoryList.add("Shampoo");
//                categoryList.add("Conditioner");
//                categoryList.add("Gel");
//                categoryList.add("Hair Cream");
//                pos = 0;
//            } else if (category.equalsIgnoreCase("Shampoo")) {
//                categoryList.add("Hair Color");
//                categoryList.add("Shampoo");
//                categoryList.add("Conditioner");
//                categoryList.add("Gel");
//                categoryList.add("Hair Cream");
//                pos = 1;
//            }
////            else if (category.equalsIgnoreCase("Skin Care")) {
////                categoryList.add("Scrubs");
////                categoryList.add("Lotion & Cream");
////                categoryList.add("Face Wash");
////                categoryList.add("Sun Block");
////            }
//            else if (category.equalsIgnoreCase("Dental Care")) {
//                categoryList.add("Tooth Brush");
//                categoryList.add("Tooth paste");
//                categoryList.add("Mouth Wash");
//                tabScrollable = true;
//            } else if (category.equalsIgnoreCase("Soap, Hand Wash & Sanitizer")) {
//                categoryList.add("Soap");
//                categoryList.add("Hand Wash");
//                categoryList.add("Shower Gel");
//                tabScrollable = true;
//            } else if (category.equalsIgnoreCase("Shoes Polish & Brush")) {
//                categoryList.add("Polish");
//                categoryList.add("Brush");
//                tabScrollable = true;
//            } else if (category.equalsIgnoreCase("Fruits")) {
//                categoryList.add("Vegetables");
//                categoryList.add("Fruits");
//                pos = 1;
//                tabScrollable = true;
//
//            } else if (category.equalsIgnoreCase("Vegetables")) {
//                categoryList.add("Vegetables");
//                categoryList.add("Fruits");
//                pos = 0;
//                tabScrollable = true;
//            } else if (category.equalsIgnoreCase("Dairy")) {
//                categoryList.add("Dairy");
//                categoryList.add("Bakery");
//                categoryList.add("Beverages");
//                categoryList.add("Milk & Yogurt");
//                categoryList.add("Breads");
//                categoryList.add("Eggs");
//
//                categoryList.add("Mineral Water");
//                pos = 0;
//
//            } else if (category.equalsIgnoreCase("Bakery")) {
//                categoryList.add("Dairy");
//                categoryList.add("Bakery");
//                categoryList.add("Beverages");
//                categoryList.add("Milk & Yogurt");
//                categoryList.add("Breads");
//                categoryList.add("Eggs");
//
//                categoryList.add("Mineral Water");
//                pos = 1;
//
//            } else if (category.equalsIgnoreCase("Beverages")) {
//                categoryList.add("Dairy");
//                categoryList.add("Bakery");
//                categoryList.add("Beverages");
//                categoryList.add("Milk & Yogurt");
//                categoryList.add("Breads");
//                categoryList.add("Eggs");
//                categoryList.add("Mineral Water");
//                pos = 2;
//
//            } else if (category.equalsIgnoreCase("Milk & Yogurt")) {
//                categoryList.add("Dairy");
//                categoryList.add("Bakery");
//                categoryList.add("Beverages");
//                categoryList.add("Milk & Yogurt");
//                categoryList.add("Breads");
//                categoryList.add("Eggs");
//
//                categoryList.add("Mineral Water");
//                pos = 3;
//
//            } else if (category.equalsIgnoreCase("Breads")) {
//                categoryList.add("Dairy");
//                categoryList.add("Bakery");
//                categoryList.add("Beverages");
//                categoryList.add("Milk & Yogurt");
//                categoryList.add("Breads");
//                categoryList.add("Eggs");
//
//                categoryList.add("Mineral Water");
//                pos = 4;
//
//            } else if (category.equalsIgnoreCase("Eggs")) {
//                categoryList.add("Dairy");
//                categoryList.add("Bakery");
//                categoryList.add("Beverages");
//                categoryList.add("Milk & Yogurt");
//                categoryList.add("Breads");
//                categoryList.add("Eggs");
//
//                categoryList.add("Mineral Water");
//                pos = 5;
//
//            } else if (category.equalsIgnoreCase("Mineral Water")) {
//                categoryList.add("Dairy");
//                categoryList.add("Bakery");
//                categoryList.add("Beverages");
//                categoryList.add("Milk & Yogurt");
//                categoryList.add("Breads");
//                categoryList.add("Eggs");
//
//                categoryList.add("Mineral Water");
//                pos = 6;
//
//            } else if (category.equalsIgnoreCase("Laundry")) {
//                categoryList.add("Laundry");
//                categoryList.add("Kitchen Cleaning");
//                categoryList.add("Floor & Bath Cleaning");
//                categoryList.add("Repellents");
//                pos = 0;
//
//            } else if (category.equalsIgnoreCase("Kitchen Cleaning")) {
//                categoryList.add("Laundry");
//                categoryList.add("Kitchen Cleaning");
//                categoryList.add("Floor & Bath Cleaning");
//                categoryList.add("Repellents");
//
//                pos = 1;
//
//            } else if (category.equalsIgnoreCase("Floor & Bath Cleaning")) {
//                categoryList.add("Laundry");
//                categoryList.add("Kitchen Cleaning");
//                categoryList.add("Floor & Bath Cleaning");
//                categoryList.add("Repellents");
//
//                pos = 2;
//
//            } else if (category.equalsIgnoreCase("Repellents")) {
//                categoryList.add("Laundry");
//                categoryList.add("Kitchen Cleaning");
//                categoryList.add("Floor & Bath Cleaning");
//                categoryList.add("Repellents");
//
//                pos = 3;
//
//            }
////            else if (category.equalsIgnoreCase("Cold Drinks")) {
////                categoryList.add("Cold Drinks");
////                categoryList.add("Juices");
////                categoryList.add("Tea");
////                categoryList.add("Mineral Water");
////                categoryList.add("Sharbat");
////                categoryList.add("Coffee");
////
////                pos = 0;
////
////
////            }
////            else if (category.equalsIgnoreCase("Juices")) {
////                categoryList.add("Cold Drinks");
////                categoryList.add("Juices");
////                categoryList.add("Tea");
////                categoryList.add("Mineral Water");
////                categoryList.add("Sharbat");
////                categoryList.add("Coffee");
////
////                pos = 1;
////
////            }
////            else if (category.equalsIgnoreCase("Tea")) {
////                categoryList.add("Cold Drinks");
////                categoryList.add("Juices");
////                categoryList.add("Tea");
////                categoryList.add("Mineral Water");
////                categoryList.add("Sharbat");
////                categoryList.add("Coffee");
////
////                pos = 2;
////
////            }
////            else if (category.equalsIgnoreCase("Mineral Water")) {
//////                categoryList.add("Cold Drinks");
//////                categoryList.add("Juices");
//////                categoryList.add("Tea");
////                categoryList.add("Mineral Water");
//////                categoryList.add("Sharbat");
//////                categoryList.add("Coffee");
////                pos = 0;
////
////            }
////            else if (category.equalsIgnoreCase("Sharbat")) {
////                categoryList.add("Cold Drinks");
////                categoryList.add("Juices");
////                categoryList.add("Tea");
////                categoryList.add("Mineral Water");
////                categoryList.add("Sharbat");
////                categoryList.add("Coffee");
////
////                pos = 4;
////
////            }
////            else if (category.equalsIgnoreCase("Coffee")) {
////                categoryList.add("Cold Drinks");
////                categoryList.add("Juices");
////                categoryList.add("Tea");
////                categoryList.add("Mineral Water");
////                categoryList.add("Sharbat");
////                categoryList.add("Coffee");
////
////                pos = 5;
////
////            }
//
//        }

        adapter = new FragmentAdapter(
                this, getSupportFragmentManager(), categoryList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos);

        // Give the TabLayout the ViewPager
        tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ListOfProducts.this.setTitle(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    private void getCategoriesFromDB() {
        mDatabase.child("Categories").child("ChildCategory").child(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChildCategoryModel model = snapshot.getValue(ChildCategoryModel.class);
                        if (model != null) {
                            categoryList.add(model);

                            Collections.sort(categoryList, new Comparator<ChildCategoryModel>() {
                                @Override
                                public int compare(ChildCategoryModel listData, ChildCategoryModel t1) {
                                    Integer ob1 = listData.getPosition();
                                    Integer ob2 = t1.getPosition();

                                    return ob1.compareTo(ob2);

                                }
                            });

                        }

                    }
                    if (categoryList.size() > 3) {
                        tabScrollable = false;
                    } else {
                        tabScrollable = true;
                    }
                    if (tabScrollable) {
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    } else {

                    }
                    adapter.notifyDataSetChanged();

                } else {
                    categoryList.clear();
                    adapter.notifyDataSetChanged();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        mDatabase.child("customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItemCountFromDb = dataSnapshot.getChildrenCount();
                textCartItemCount.setText("" + cartItemCountFromDb);
                SharedPrefs.setCartCount("" + cartItemCountFromDb);
                if (dataSnapshot.getChildrenCount() == 0) {
                    SharedPrefs.setCartCount("0");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }
        if (id == R.id.action_search) {
            Intent i = new Intent(ListOfProducts.this, Search.class);
            startActivity(i);
        } else if (id == R.id.action_cart) {
//            if (SharedPrefs.getIsLoggedIn().equals("yes")) {
            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
                CommonUtils.showToast("Your Cart is empty");
            } else {
                Intent i = new Intent(ListOfProducts.this, Cart.class);
                startActivity(i);
            }
//            } else {
//                Intent i = new Intent(ListOfProducts.this, Login.class);
//                i.putExtra("takeUserToActivity", Constants.CART_ACTIVITY);
//                startActivity(i);
//            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
