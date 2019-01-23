package com.clicknshop.goshop.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clicknshop.goshop.Activities.Login;
import com.clicknshop.goshop.Activities.ViewProduct;
import com.clicknshop.goshop.Interface.AddToCartInterface;
import com.clicknshop.goshop.Models.Product;
import com.clicknshop.goshop.Models.ProductCountModel;
import com.clicknshop.goshop.R;
import com.bumptech.glide.Glide;
import com.clicknshop.goshop.Utils.SharedPrefs;

import java.util.ArrayList;

/**
 * Created by AliAh on 23/06/2018.
 */

public class RelatedProductsAdapter extends RecyclerView.Adapter<RelatedProductsAdapter.Viewholder> {
    Context context;
    ArrayList<Product> productList;
    AddToCartInterface addToCartInterface;
    ArrayList<ProductCountModel> userCartProductList;

    public RelatedProductsAdapter(Context context,
                                  ArrayList<Product> productList,
                                  ArrayList<ProductCountModel> userCartProductList,
                                  AddToCartInterface addToCartInterface) {
        this.context = context;
        this.productList = productList;
        this.addToCartInterface = addToCartInterface;
        this.userCartProductList = userCartProductList;
    }

    @NonNull
    @Override
    public RelatedProductsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.related_product_layout, parent, false);
        RelatedProductsAdapter.Viewholder viewHolder = new RelatedProductsAdapter.Viewholder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RelatedProductsAdapter.Viewholder holder, final int position) {
        final Product model = productList.get(position);

        holder.title.setText(model.getTitle());
        holder.price.setText("TSh. " + model.getPrice());
        holder.subtitle.setText(model.getSubtitle());
        Glide.with(context).load(model.getThumbnailUrl()).into(holder.image);

        final int[] count = {1};
        ProductCountModel productCountModel = null;
        boolean flag = false;
        for (int i = 0; i < userCartProductList.size(); i++) {
            if (model.getId().equals(userCartProductList.get(i).getProduct().getId())) {
                flag = true;
                productCountModel = userCartProductList.get(i);
            }
        }
        if (flag) {
            holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
            holder.count.setTextColor(context.getResources().getColor(R.color.default_grey));
            count[0] = productCountModel.getQuantity();
            holder.count.setText("" + count[0]);
            holder.increase.setVisibility(View.VISIBLE);

            if (count[0] > 1) {
                holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                holder.decrease.setVisibility(View.VISIBLE);
            } else {
                holder.decrease.setImageResource(R.drawable.delete);
                holder.decrease.setVisibility(View.VISIBLE);
            }
        } else {
            holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_colored);
            holder.count.setTextColor(context.getResources().getColor(R.color.colorWhite));
            holder.count.setText("Add to cart");
            holder.increase.setVisibility(View.GONE);
            holder.decrease.setVisibility(View.GONE);

        }
        flag = false;

        holder.count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefs.getIsLoggedIn().equals("yes")) {
                    if (count[0] > 1) {

                    } else {
                        holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                        holder.count.setTextColor(context.getResources().getColor(R.color.default_grey));

                        holder.count.setText("" + count[0]);
                        holder.increase.setVisibility(View.VISIBLE);
                        holder.decrease.setVisibility(View.VISIBLE);
                        addToCartInterface.addedToCart(model, count[0], position);
                    }
                } else {
                    Intent i = new Intent(context, Login.class);
                    context.startActivity(i);
                }
            }
        });
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count[0] += 1;
                holder.count.setText("" + count[0]);
                holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                addToCartInterface.quantityUpdate(model, count[0], position);

            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count[0] > 2) {
                    count[0] -= 1;
                    holder.count.setText("" + count[0]);
                    addToCartInterface.quantityUpdate(model, count[0], position);


                } else if (count[0] > 1) {
                    {
                        count[0] -= 1;
                        holder.count.setText("" + count[0]);
                        holder.decrease.setImageResource(R.drawable.delete);
                        addToCartInterface.quantityUpdate(model, count[0], position);


                    }
                } else if (count[0] == 1) {
                    holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_colored);
                    holder.count.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    holder.count.setText("Add to cart");
                    holder.increase.setVisibility(View.GONE);
                    holder.decrease.setVisibility(View.GONE);
                    addToCartInterface.deletedFromCart(model, position);

                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewProduct.class);
                i.putExtra("productId", model.getId());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title, subtitle, price, count;
        ImageView image, increase, decrease;
        RelativeLayout relativeLayout;

        public Viewholder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            count = itemView.findViewById(R.id.count);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
