package com.urraan.hamzakhan.ecommerece.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.urraan.hamzakhan.ecommerece.Interfaces.ItemClickListener;
import com.urraan.hamzakhan.ecommerece.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView tvProductName,tvProductDescription,tvProductPrice;
    public ImageView productImageView;
    public ItemClickListener listener;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        tvProductName = itemView.findViewById(R.id.product_name);
        tvProductDescription = itemView.findViewById(R.id.product_description);
        productImageView = itemView.findViewById(R.id.product_image);
        tvProductPrice = itemView.findViewById(R.id.product_price);
    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);

    }
}
