package com.clicknshop.goshop.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clicknshop.goshop.Interface.AddToCartInterface;
import com.clicknshop.goshop.Models.ProductCountModel;
import com.clicknshop.goshop.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AliAh on 22/06/2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    AddToCartInterface addToCartInterface;
    ArrayList<ProductCountModel> userCartProductList;

    public CartAdapter(Context context, ArrayList<ProductCountModel> userCartProductList, AddToCartInterface addToCartInterface) {
        this.context = context;
        this.addToCartInterface = addToCartInterface;
        this.userCartProductList = userCartProductList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item_layout, parent, false);
        CartAdapter.ViewHolder viewHolder = new CartAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ProductCountModel model = userCartProductList.get(position);
        holder.title.setText(model.getProduct().getTitle());
        holder.price.setText("TSh " + model.getProduct().getPrice());
        holder.oldPrice.setVisibility(View.GONE);
        holder.subtitle.setText(model.getProduct().getSubtitle());
        Glide.with(context).load(model.getProduct().getThumbnailUrl()).into(holder.image);

        holder.viewProduct.setVisibility(View.GONE);

        final int[] count = {model.getQuantity()};
        holder.increase.setVisibility(View.VISIBLE);
        holder.count.setText("" + count[0]);
        holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
        holder.count.setTextColor(context.getResources().getColor(R.color.default_grey));
        if (count[0] > 1) {

            holder.count.setOnClickListener(null);
            holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
            holder.decrease.setVisibility(View.VISIBLE);
        } else {
            holder.count.setOnClickListener(null);
            holder.decrease.setImageResource(R.drawable.delete);
            holder.decrease.setVisibility(View.VISIBLE);


        }

        holder.count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.count.setText("" + count[0]);
                holder.increase.setVisibility(View.VISIBLE);
                holder.decrease.setVisibility(View.VISIBLE);
                addToCartInterface.addedToCart(model.getProduct(), count[0], position);

            }
        });
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count[0] += 1;
                holder.count.setText("" + count[0]);
                holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                addToCartInterface.quantityUpdate(model.getProduct(), count[0], position);

            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count[0] > 2) {
                    count[0] -= 1;
                    holder.count.setText("" + count[0]);
                    addToCartInterface.quantityUpdate(model.getProduct(), count[0], position);


                } else if (count[0] > 1) {
                    {
                        count[0] -= 1;
                        holder.count.setText("" + count[0]);
                        holder.decrease.setImageResource(R.drawable.delete);
                        addToCartInterface.quantityUpdate(model.getProduct(), count[0], position);


                    }
                } else if (count[0] == 1) {
                    holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_colored);
                    holder.count.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    holder.count.setText("Add to cart");
                    holder.increase.setVisibility(View.GONE);
                    holder.decrease.setVisibility(View.GONE);
                    addToCartInterface.deletedFromCart(model.getProduct(), position);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userCartProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, price, count, oldPrice;
        RelativeLayout relativeLayout;
        ImageView image, increase, decrease, viewProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            count = itemView.findViewById(R.id.count);
            viewProduct = itemView.findViewById(R.id.viewProduct);
            oldPrice = itemView.findViewById(R.id.oldPrice);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);


        }
    }
}
