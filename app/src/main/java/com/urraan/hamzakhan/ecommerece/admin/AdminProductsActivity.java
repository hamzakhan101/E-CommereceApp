package com.urraan.hamzakhan.ecommerece.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.urraan.hamzakhan.ecommerece.Models.ProductModel;
import com.urraan.hamzakhan.ecommerece.R;
import com.urraan.hamzakhan.ecommerece.ViewHolders.ProductViewHolder;
import com.urraan.hamzakhan.ecommerece.ui.home.HomeViewModel;

public class AdminProductsActivity extends AppCompatActivity {

    private HomeViewModel homeViewModel;
    private DatabaseReference productReference;
    private RecyclerView recyclerMenu;
    private LinearLayoutManager layoutManager;
    private String type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_products);
        productReference = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerMenu = findViewById(R.id.recycler_menu_admin);
        layoutManager = new LinearLayoutManager(this);
        recyclerMenu.setHasFixedSize(true);
        recyclerMenu.setLayoutManager(layoutManager);

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<ProductModel> options = new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(productReference,ProductModel.class)
                .build();
        FirebaseRecyclerAdapter<ProductModel, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<ProductModel, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final ProductModel productModel) {
                productViewHolder.tvProductName.setText(productModel.getPname());
                productViewHolder.tvProductDescription.setText(productModel.getDescription());
                productViewHolder.tvProductPrice.setText("Price = " + productModel.getPrice() + "$");
                Picasso.get().load(productModel.getImage()).into(productViewHolder.productImageView);
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent =  new Intent(AdminProductsActivity.this, AdminMaintainProductActivity.class);
                        intent.putExtra("pid",productModel.getPid());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerMenu.setAdapter(adapter);
        adapter.startListening();
    }
}
