package com.clicknshop.goshop.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clicknshop.goshop.Adapters.MainSliderAdapter;
import com.clicknshop.goshop.Adapters.NewCategoryAdapter;
import com.clicknshop.goshop.Adapters.RelatedProductsAdapter;
import com.clicknshop.goshop.BannerModel;
import com.clicknshop.goshop.Interface.AddToCartInterface;
import com.clicknshop.goshop.Models.NewCategoryModel;
import com.clicknshop.goshop.Models.NewTestModel;
import com.clicknshop.goshop.Models.Product;
import com.clicknshop.goshop.Models.ProductCountModel;
import com.clicknshop.goshop.Models.SubCategoryModel;
import com.clicknshop.goshop.R;
import com.clicknshop.goshop.Utils.CommonUtils;
import com.clicknshop.goshop.Utils.Constants;
import com.clicknshop.goshop.Utils.PrefManager;
import com.clicknshop.goshop.Utils.SharedPrefs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    DatabaseReference mDatabase;
    long cartItemCountFromDb;
    Toolbar toolbar;
    MainSliderAdapter mViewPagerAdapter;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    ArrayList<BannerModel> pics = new ArrayList<>();
    ViewPager viewPager;
    public static int currentPic = 0;
    //    TextView contact;
    private String adminNumber;
    RelatedProductsAdapter relatedProductsAdapter;
    ProgressBar progress;
    RecyclerView recyclerview;
    private ArrayList<ProductCountModel> userCartProductList = new ArrayList<>();
    private ArrayList<Product> productArrayList = new ArrayList<>();
    GoogleApiClient apiClient;

//    ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        splash = findViewById(R.id.splash);
        progress = findViewById(R.id.progress);
        recyclerview = findViewById(R.id.recyclerview);

        this.setTitle("Go Shop");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        apiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();


        initViewPager();
        initNewRecycler();
        getLatestProductsFromDB();
        sendFcmKeyToServer();

//        initGrocery();
//        initDairy();
//        initCare();
//        initFruits();
//        initHomeCare();
//        initBeverages();


        initDrawer();
        getAdminDataFromDB();
    }


    private void getLatestProductsFromDB() {
        final ArrayList<NewCategoryModel> arrayList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        relatedProductsAdapter = new RelatedProductsAdapter(MainActivity.this, productArrayList, userCartProductList, new AddToCartInterface() {
            @Override
            public void addedToCart(final Product product, final int quantity, int position) {
                mDatabase.child("customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).setValue(new ProductCountModel(product, quantity, System.currentTimeMillis()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void deletedFromCart(final Product product, int position) {
                mDatabase.child("customers").child(SharedPrefs.getUsername())
                        .child("cart").child(product.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getUserCartProductsFromDB();
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });
        recyclerView.setAdapter(relatedProductsAdapter);

        productArrayList.clear();
        mDatabase.child("Products").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            if (product.getIsActive().equals("true")) {
                                productArrayList.add(product);
                                Collections.sort(productArrayList, new Comparator<Product>() {
                                    @Override
                                    public int compare(Product listData, Product t1) {
                                        Long ob1 = listData.getTime();
                                        Long ob2 = t1.getTime();

                                        return ob1.compareTo(ob2);

                                    }
                                });

                            }
                        }


                    }
                    relatedProductsAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getUserCartProductsFromDB() {
        userCartProductList.clear();
        mDatabase.child("customers").child(SharedPrefs.getUsername()).child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
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

                        }
                    }
                    relatedProductsAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendFcmKeyToServer() {
        if (SharedPrefs.getIsLoggedIn().equals("yes") && !SharedPrefs.getFcmKey().equalsIgnoreCase("")) {
            mDatabase.child("customers").child(SharedPrefs.getUsername()).child("fcmKey").setValue(SharedPrefs.getFcmKey());
        }

    }

    private void initNewRecycler() {
        final ArrayList<NewCategoryModel> arrayList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recycler_grocery);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final NewCategoryAdapter adapter = new NewCategoryAdapter(MainActivity.this, arrayList);
        recyclerView.setAdapter(adapter);

        mDatabase.child("Categories").child("SubCategories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //personal care

                        ArrayList<SubCategoryModel> sub = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.child("categories").getChildren()) {  //dental care,hair care
                            SubCategoryModel mod = snapshot1.getValue(SubCategoryModel.class);
                            if (mod != null) {
                                sub.add(mod);
                                Collections.sort(sub, new Comparator<SubCategoryModel>() {
                                    @Override
                                    public int compare(SubCategoryModel listData, SubCategoryModel t1) {
                                        Integer ob1 = listData.getPosition();
                                        Integer ob2 = t1.getPosition();

                                        return ob1.compareTo(ob2);

                                    }
                                });
                            }
                        }
                        int pos = snapshot.child("position").getValue(Integer.class);
                        arrayList.add(new NewCategoryModel(pos, snapshot.getKey(), sub));

                        Collections.sort(arrayList, new Comparator<NewCategoryModel>() {
                            @Override
                            public int compare(NewCategoryModel listData, NewCategoryModel t1) {
                                Integer ob1 = listData.getPosition();
                                Integer ob2 = t1.getPosition();

                                return ob1.compareTo(ob2);

                            }
                        });

                    }


                    progress.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();
                } else

                {
                    arrayList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAdminDataFromDB() {
        mDatabase.child("Settings").child("AdminNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    adminNumber = dataSnapshot.getValue(String.class);
                    SharedPrefs.setAdminPhone(adminNumber);
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


//        pics.add("https://firebasestorage.googleapis.com/v0/b/grocerryapp-6924e.appspot.com/o/Photos%2Flipton.jpg?alt=media&token=f296bfcd-81ca-45af-a442-8952649ec7b4");
//        pics.add("https://firebasestorage.googleapis.com/v0/b/grocerryapp-6924e.appspot.com/o/Photos%2Fsurf.jpg?alt=media&token=ef3e5867-0d6e-4acf-9e91-048096d84faa");


    }

//    private void initGrocery() {
//        ArrayList<GroceryListModel> arrayList = new ArrayList<GroceryListModel>();
//        RecyclerView recyclerView = findViewById(R.id.recycler_grocery);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        GroceryAdapter adapter = new GroceryAdapter(MainActivity.this, arrayList);
//        recyclerView.setAdapter(adapter);
//
//        GroceryListModel m = new GroceryListModel("Oil & Ghee", R.drawable.oil);
//        arrayList.add(m);
//        m = new GroceryListModel("Spices, Salt & Sugar", R.drawable.spices);
//        arrayList.add(m);
//        m = new GroceryListModel("Daalain, Rice & Flour", R.drawable.dalain);
//        arrayList.add(m);
//        m = new GroceryListModel("Sauces, Olives & Pickles", R.drawable.sauces);
//        arrayList.add(m);
//        m = new GroceryListModel("Jam, Honey & Spread", R.drawable.jam);
//        arrayList.add(m);
//        m = new GroceryListModel("Baking & Desert", R.drawable.baking);
//        arrayList.add(m);
//
//        adapter.notifyDataSetChanged();
//
//    }
//
//    private void initDairy() {
//        ArrayList<GroceryListModel> arrayList = new ArrayList<GroceryListModel>();
//        RecyclerView recyclerView = findViewById(R.id.recycler_dairy);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        GroceryAdapter adapter = new GroceryAdapter(MainActivity.this, arrayList);
//        recyclerView.setAdapter(adapter);
//
//        GroceryListModel m = new GroceryListModel("Dairy", R.drawable.dairy);
//        arrayList.add(m);
//        m = new GroceryListModel("Bakery", R.drawable.cake);
//        arrayList.add(m);
//        m = new GroceryListModel("Beverages", R.drawable.juices);
//        arrayList.add(m);
//        m = new GroceryListModel("Milk & Yogurt", R.drawable.milk);
//        arrayList.add(m);
//        m = new GroceryListModel("Breads", R.drawable.bread);
//        arrayList.add(m);
//        m = new GroceryListModel("Eggs", R.drawable.eggs);
//        arrayList.add(m);
//        m = new GroceryListModel("Mineral Water", R.drawable.water);
//        arrayList.add(m);
//
//        adapter.notifyDataSetChanged();
//    }
//
//
//    private void initCare() {
//        ArrayList<GroceryListModel> arrayList = new ArrayList<GroceryListModel>();
//        RecyclerView recyclerView = findViewById(R.id.recycler_care);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        GroceryAdapter adapter = new GroceryAdapter(MainActivity.this, arrayList);
//        recyclerView.setAdapter(adapter);
//
//        GroceryListModel m = new GroceryListModel("Hair Care", R.drawable.hair);
//        arrayList.add(m);
//        m = new GroceryListModel("Dental Care", R.drawable.dentalcare);
//        arrayList.add(m);
//        m = new GroceryListModel("Soap, Hand Wash & Sanitizer", R.drawable.soap);
//        arrayList.add(m);
//
//        m = new GroceryListModel("Shoes Ploish & Brush", R.drawable.polish);
//        arrayList.add(m);
//
//
//        adapter.notifyDataSetChanged();
//
//    }
//
//    private void initFruits() {
//        ArrayList<GroceryListModel> arrayList = new ArrayList<GroceryListModel>();
//        RecyclerView recyclerView = findViewById(R.id.recycler_fruits);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        GroceryAdapter adapter = new GroceryAdapter(MainActivity.this, arrayList);
//        recyclerView.setAdapter(adapter);
//
//        GroceryListModel m = new GroceryListModel("Vegetables", R.drawable.vegetable);
//        arrayList.add(m);
//        m = new GroceryListModel("Fruits", R.drawable.fruits);
//        arrayList.add(m);
//
//
//        adapter.notifyDataSetChanged();
//
//    }
//
//    private void initHomeCare() {
//        ArrayList<GroceryListModel> arrayList = new ArrayList<GroceryListModel>();
//        RecyclerView recyclerView = findViewById(R.id.recycler_home_care);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        GroceryAdapter adapter = new GroceryAdapter(MainActivity.this, arrayList);
//        recyclerView.setAdapter(adapter);
//
//        GroceryListModel m = new GroceryListModel("Laundry", R.drawable.laundry);
//        arrayList.add(m);
//
//        m = new GroceryListModel("Kitchen Cleaning", R.drawable.kitchencleaning);
//        arrayList.add(m);
//        m = new GroceryListModel("Floor & Bath Cleaning", R.drawable.floor);
//        arrayList.add(m);
//        m = new GroceryListModel("Repellents", R.drawable.repellents);
//        arrayList.add(m);
//
//
//        adapter.notifyDataSetChanged();
//    }
//
//    private void initBeverages() {
//        ArrayList<GroceryListModel> arrayList = new ArrayList<GroceryListModel>();
//        RecyclerView recyclerView = findViewById(R.id.recycler_beverages);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        GroceryAdapter adapter = new GroceryAdapter(MainActivity.this, arrayList);
//        recyclerView.setAdapter(adapter);
//
//        GroceryListModel m = new GroceryListModel("Cold Drinks", R.drawable.colddrinks);
////        arrayList.add(m);
////        m = new GroceryListModel("Juices", R.drawable.juices);
////        arrayList.add(m);
////
////        m = new GroceryListModel("Tea", R.drawable.tea);
////        arrayList.add(m);
//        m = new GroceryListModel("Mineral Water", R.drawable.water);
//        arrayList.add(m);
////        m = new GroceryListModel("Sharbat", R.drawable.sharbat);
////        arrayList.add(m);
////        m = new GroceryListModel("Coffee", R.drawable.coffee);
////        arrayList.add(m);
//
//        adapter.notifyDataSetChanged();
//    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
            }

            mBackPressed = System.currentTimeMillis();
        }

    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.name_drawer);
        TextView navSubtitle = (TextView) headerView.findViewById(R.id.phone_drawer);


        Menu nav_Menu = navigationView.getMenu();


        if (SharedPrefs.getUsername().equalsIgnoreCase("")) {
            nav_Menu.findItem(R.id.signout).setVisible(false);
            navSubtitle.setText("Welcome to Go Shop");

            navUsername.setText("Login or Signup");
            navUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                }
            });
        } else {
            navSubtitle.setText(SharedPrefs.getPhone());

            navUsername.setText(SharedPrefs.getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);


        View actionView = MenuItemCompat.getActionView(menuItem);
        final TextView textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent i = new Intent(MainActivity.this, Search.class);
            startActivity(i);
        } else if (id == R.id.action_cart) {
            if (SharedPrefs.getCartCount().equalsIgnoreCase("0")) {
                CommonUtils.showToast("Your Cart is empty");
            } else {
                Intent i = new Intent(MainActivity.this, Cart.class);
                startActivity(i);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getUserCartProductsFromDB();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        int id = item.getItemId();


        if (id == R.id.profile) {
            if (SharedPrefs.getIsLoggedIn().equals("yes")) {
                Intent i = new Intent(MainActivity.this, MyProfile.class);
                startActivity(i);
            } else {
                Intent i = new Intent(MainActivity.this, Login.class);

                startActivity(i);
            }
        } else if (id == R.id.orders) {
            if (SharedPrefs.getIsLoggedIn().equals("yes")) {
                Intent i = new Intent(MainActivity.this, MyOrders.class);
                startActivity(i);
            } else {
                Intent i = new Intent(MainActivity.this, Login.class);
                i.putExtra("takeUserToActivity", Constants.MY_ORDERS_ACTIVITY);

                startActivity(i);
            }
        } else if (id == R.id.chat) {
            if (SharedPrefs.getIsLoggedIn().equals("yes")) {
                Intent i = new Intent(MainActivity.this, LiveChat.class);
                i.putExtra("takeUserToActivity", Constants.LIVE_CHAT);

                startActivity(i);
            } else {
                Intent i = new Intent(MainActivity.this, Login.class);

                startActivity(i);
            }


        } else if (id == R.id.callus) {


            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + SharedPrefs.getAdminPhone()));
            startActivity(i);
        } else if (id == R.id.privacy) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://grocerryapp-6924e.firebaseio.com/Admin/privacyPolicy.json"));
            startActivity(i);
        } else if (id == R.id.share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Go Shop\n Download Now\n" + "http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share App via.."));


        } else if (id == R.id.signout) {

            PrefManager prefManager = new PrefManager(this);
            prefManager.setFirstTimeLaunch(true);
            SharedPrefs.logout();
            Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                    finish();
                }
            });


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deleteAppData() {
        try {
            // clearing app data
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + packageName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
