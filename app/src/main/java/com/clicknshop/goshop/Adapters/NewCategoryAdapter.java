package com.clicknshop.goshop.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clicknshop.goshop.Activities.ListOfProducts;
import com.clicknshop.goshop.Models.NewCategoryModel;
import com.clicknshop.goshop.Models.SubCategoryModel;
import com.clicknshop.goshop.R;
import com.clicknshop.goshop.Utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by AliAh on 26/06/2018.
 */

public class NewCategoryAdapter extends RecyclerView.Adapter<NewCategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<NewCategoryModel> itemList;

    public NewCategoryAdapter(Context context, ArrayList<NewCategoryModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grocery_layout, parent, false);
        NewCategoryAdapter.ViewHolder holder = new NewCategoryAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final NewCategoryModel model = itemList.get(position);
        holder.title.setText(model.getName());
        holder.recycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        CategoriesAdapter adapter = new CategoriesAdapter(context, model.getModel());
        holder.recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RecyclerView recycler;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            recycler = itemView.findViewById(R.id.recycler);
        }
    }
}
