package com.clicknshop.goshop.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.clicknshop.goshop.Adapters.NotificationHistoryAdapter;
import com.clicknshop.goshop.Models.Customer;
import com.clicknshop.goshop.Models.CustomerNotificationModel;
import com.clicknshop.goshop.Models.ProductCountModel;
import com.clicknshop.goshop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class NotificationHistory extends AppCompatActivity {
    ArrayList<CustomerNotificationModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;
    NotificationHistoryAdapter adapter;
    RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Notification history");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerview = findViewById(R.id.recyclerview);
        adapter = new NotificationHistoryAdapter(this, itemList);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(adapter);
        getDataFromDB();
    }

    private void getDataFromDB() {
        mDatabase.child("CustomerNotifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CustomerNotificationModel model = snapshot.getValue(CustomerNotificationModel.class);
                        if (model != null) {
                            itemList.add(model);
                        }
                    }
                    Collections.sort(itemList, new Comparator<CustomerNotificationModel>() {
                        @Override
                        public int compare(CustomerNotificationModel listData, CustomerNotificationModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob2.compareTo(ob1);

                        }
                    });


                    adapter.notifyDataSetChanged();
                } else {
                    itemList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
