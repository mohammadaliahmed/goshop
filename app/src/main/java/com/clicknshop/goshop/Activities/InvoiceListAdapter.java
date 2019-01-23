package com.clicknshop.goshop.Activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clicknshop.goshop.Models.ProductCountModel;
import com.clicknshop.goshop.R;


import java.util.ArrayList;

class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.ViewHolder> {
    Context context;
    ArrayList<ProductCountModel> itemList;

    public InvoiceListAdapter(Context context, ArrayList<ProductCountModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invoice_item_list, parent, false);
        InvoiceListAdapter.ViewHolder viewHolder = new InvoiceListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCountModel model = itemList.get(position);
        holder.serial.setText("" + (position + 1));
        holder.productName.setText(model.getProduct().getTitle());
        holder.unit.setText("" + model.getQuantity());
        holder.qIntoP.setText(model.getQuantity() + "*" + model.getProduct().getPrice());
        holder.tPrice.setText(""+model.getQuantity() * model.getProduct().getPrice());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serial, productName, unit, qIntoP, tPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            serial = itemView.findViewById(R.id.serial);
            productName = itemView.findViewById(R.id.productName);
            unit = itemView.findViewById(R.id.unit);
            qIntoP = itemView.findViewById(R.id.qIntoP);
            tPrice = itemView.findViewById(R.id.tPrice);
        }
    }
}
