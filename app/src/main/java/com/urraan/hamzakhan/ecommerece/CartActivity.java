package com.urraan.hamzakhan.ecommerece;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.urraan.hamzakhan.ecommerece.Models.CartModel;
import com.urraan.hamzakhan.ecommerece.Prevalent.Prevalent;
import com.urraan.hamzakhan.ecommerece.ViewHolders.CartViewHolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextBtn;
    private TextView tvtotalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        nextBtn = findViewById(R.id.btn_next_process);
        tvtotalPrice = findViewById(R.id.tv_total_price);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CartActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<CartModel> options = new FirebaseRecyclerOptions.Builder<CartModel>()
                .setQuery(cartRef.child("User View")
                        .child(Prevalent.CurrentOnlineUser.getPhone()).child("Products"),CartModel.class).build();
        FirebaseRecyclerAdapter<CartModel, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<CartModel, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull CartModel cartModel) {
                        cartViewHolder.tvProductName.setText(cartModel.getPname());
                        cartViewHolder.tvProductPrice.setText("Price " + cartModel.getPrice() + "$");
                        cartViewHolder.tvProductQty.setText("Quantity" + cartModel.getQuantity());
                        cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Edit",
                                                "Remove"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder cartViewHolder = new CartViewHolder(view);
                        return cartViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}