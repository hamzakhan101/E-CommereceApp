package com.urraan.hamzakhan.ecommerece.ViewHolders;

import android.view.View;
import android.widget.TextView;

import com.urraan.hamzakhan.ecommerece.Interfaces.ItemClickListener;
import com.urraan.hamzakhan.ecommerece.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tvProductName,tvProductPrice,tvProductQty;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        tvProductName = itemView.findViewById(R.id.cart_product_name);
        tvProductPrice = itemView.findViewById(R.id.cart_product_price);
        tvProductQty = itemView.findViewById(R.id.cart_product_quantity);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
