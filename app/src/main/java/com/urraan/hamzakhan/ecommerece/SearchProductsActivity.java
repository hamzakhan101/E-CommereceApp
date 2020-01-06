package com.urraan.hamzakhan.ecommerece;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.urraan.hamzakhan.ecommerece.Models.ProductModel;
import com.urraan.hamzakhan.ecommerece.ViewHolders.ProductViewHolder;

public class SearchProductsActivity extends AppCompatActivity {

    private Button btnSearch;
    private EditText inputSearch;
    private RecyclerView searchList;
    private String searchText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);
        btnSearch = findViewById(R.id.btn_search);
        inputSearch = findViewById(R.id.search_product_name);
        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(this));

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = inputSearch.getText().toString();

                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(reference.orderByChild("pname").startAt(searchText),ProductModel.class)
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
                        Intent intent =  new Intent(SearchProductsActivity.this, ProductsDetailActivity.class);
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
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}
