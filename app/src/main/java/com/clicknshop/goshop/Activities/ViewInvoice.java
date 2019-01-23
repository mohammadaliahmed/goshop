package com.clicknshop.goshop.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clicknshop.goshop.Models.InvoiceModel;
import com.clicknshop.goshop.R;
import com.clicknshop.goshop.Utils.CommonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewInvoice extends AppCompatActivity {
    TextView billNumber, orderNumber, date, dayChosen, timeChosen,
            customerName, mobileNumber, address, comments, deliveryCharges, total;
    RecyclerView recycler;
    DatabaseReference mDatabase;
    String invoiceId;
    InvoiceModel model;

    InvoiceListAdapter adapter;
    RelativeLayout wholeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invoice);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        invoiceId = getIntent().getStringExtra("invoiceId");
        this.setTitle("Bill Number: " + invoiceId);

        billNumber = findViewById(R.id.billNumber);
        orderNumber = findViewById(R.id.orderNumber);
        date = findViewById(R.id.date);
        dayChosen = findViewById(R.id.dayChosen);
        timeChosen = findViewById(R.id.timeChosen);
        customerName = findViewById(R.id.customerName);
        mobileNumber = findViewById(R.id.mobileNumber);
        address = findViewById(R.id.address);
        comments = findViewById(R.id.comments);
        deliveryCharges = findViewById(R.id.deliveryCharges);
        total = findViewById(R.id.total);
        wholeLayout = findViewById(R.id.wholeLayout);
        recycler = findViewById(R.id.recycler);


        getDataFromServer();
    }

    private void getDataFromServer() {
        mDatabase.child("Invoices").child(invoiceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    model = dataSnapshot.getValue(InvoiceModel.class);
                    if (model != null) {
//                        billNumber.setText("Bill Number: " + model.getInvoiceId());
                        orderNumber.setText("Order Number: " + model.getOrder().getOrderId());
                        date.setText("Date: " + CommonUtils.getFormattedDate(model.getOrder().getTime()));
                        dayChosen.setText("Day: " + model.getOrder().getDate());
                        timeChosen.setText("Time: " + model.getOrder().getChosenTime());
                        customerName.setText("Customer Name: " + model.getOrder().getCustomer().getName());
                        mobileNumber.setText("Cell Number: " + model.getOrder().getCustomer().getPhone());
                        address.setText("Customer Address: " + model.getOrder().getCustomer().getAddress());
                        comments.setText("Comments: " + model.getOrder().getInstructions());
                        deliveryCharges.setText("Delivery Charges: TSh " + model.getOrder().getShippingCharges());
                        total.setText("Total Bill: TSh " + model.getOrder().getTotalPrice());
                        recycler.setLayoutManager(new LinearLayoutManager(ViewInvoice.this, LinearLayoutManager.VERTICAL, false));
                        adapter = new InvoiceListAdapter(ViewInvoice.this, model.getOrder().getCountModelArrayList());
                        recycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        wholeLayout.setVisibility(View.GONE);
                    }
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
