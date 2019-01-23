package com.clicknshop.goshop.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clicknshop.goshop.Activities.ViewInvoice;
import com.clicknshop.goshop.Models.OrderModel;
import com.clicknshop.goshop.R;
import com.clicknshop.goshop.Utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by AliAh on 25/06/2018.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    Context context;
    ArrayList<OrderModel> itemList;
    OrderedProductsLayout adapter;

    public OrdersAdapter(Context context, ArrayList<OrderModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_layout, parent, false);
        OrdersAdapter.ViewHolder viewHolder = new OrdersAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
        final OrderModel model = itemList.get(position);

        if (model.getBillNumber() != 0) {
            holder.viewBill.setVisibility(View.VISIBLE);
        } else {
            holder.viewBill.setVisibility(View.GONE);
        }

        holder.order.setText("Order Id: " + model.getOrderId()
                + "\n\nOrder Status: " + model.getOrderStatus()
                + "\n\nTotal amount: TSh." + model.getTotalPrice()
        );
        holder.time.setText("Order Time: " + CommonUtils.getFormattedDate(model.getTime()));
        holder.serial.setText((position + 1) + ")");
        holder.viewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewInvoice.class);
                i.putExtra("invoiceId", "" + model.getBillNumber());
                context.startActivity(i);
            }
        });


        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.recycler_order_products.setLayoutManager(layoutManager);
        adapter = new OrderedProductsLayout(context, model.getCountModelArrayList());

        holder.recycler_order_products.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    public int getItemCount() {
        return itemList==null?0:itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serial, order, time, viewBill;
        RecyclerView recycler_order_products;

        public ViewHolder(View itemView) {
            super(itemView);
            serial = itemView.findViewById(R.id.serial);
            recycler_order_products = itemView.findViewById(R.id.recycler_order_products);
            order = itemView.findViewById(R.id.order);
            time = itemView.findViewById(R.id.time);
            viewBill = itemView.findViewById(R.id.viewBill);
        }
    }
}
