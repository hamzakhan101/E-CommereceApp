package com.urraan.hamzakhan.ecommerece;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.urraan.hamzakhan.ecommerece.Models.CartModel;
import com.urraan.hamzakhan.ecommerece.ViewHolders.CartViewHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminDisplayProductsActivity extends AppCompatActivity {
    private RecyclerView cartList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartRef;
    private String userID = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_display_products);

        cartList = findViewById(R.id.ordered_products_list);
        cartList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        cartList.setLayoutManager(layoutManager);
        userID = getIntent().getStringExtra("uid");
        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View")
        .child(userID).child("Products");




    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<CartModel> options =
                new FirebaseRecyclerOptions.Builder<CartModel>()
                .setQuery(cartRef,CartModel.class)
                .build();
        FirebaseRecyclerAdapter<CartModel, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<CartModel, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull CartModel cartModel) {
                        cartViewHolder.tvProductName.setText(cartModel.getPname());
                        cartViewHolder.tvProductPrice.setText("Price " + cartModel.getPrice() + "$");
                        cartViewHolder.tvProductQty.setText("Quantity" + cartModel.getQuantity());
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder viewHolder = new CartViewHolder(view);
                        return viewHolder;
                    }
                };
        cartList.setAdapter(adapter);
        adapter.startListening();
    }
}