package com.clicknshop.goshop.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clicknshop.goshop.Activities.MainActivity;
import com.clicknshop.goshop.Activities.ProductsFromThatBrand;
import com.clicknshop.goshop.Activities.ViewProduct;
import com.clicknshop.goshop.Models.CustomerNotificationModel;
import com.clicknshop.goshop.R;

import java.util.ArrayList;

public class NotificationHistoryAdapter extends RecyclerView.Adapter<NotificationHistoryAdapter.ViewHolder> {
    Context context;
    ArrayList<CustomerNotificationModel> itemList = new ArrayList<>();

    public NotificationHistoryAdapter(Context context, ArrayList<CustomerNotificationModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item_layout, parent, false);
        NotificationHistoryAdapter.ViewHolder viewHolder = new NotificationHistoryAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CustomerNotificationModel model = itemList.get(position);
        holder.title.setText(model.getTitle());
        holder.message.setText(model.getMessage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = null;
                if (model.getType().equalsIgnoreCase("marketing")) {
                    resultIntent = new Intent(context, MainActivity.class);
                } else if (model.getType().equalsIgnoreCase("brand")) {
                    resultIntent = new Intent(context, ProductsFromThatBrand.class);
                    resultIntent.putExtra("brand", model.getId());
                } else if (model.getType().equalsIgnoreCase("product")) {
                    resultIntent = new Intent(context, ViewProduct.class);
                    resultIntent.putExtra("productId", model.getId());
                }
                context.startActivity(resultIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            message = itemView.findViewById(R.id.message);
        }
    }
}
