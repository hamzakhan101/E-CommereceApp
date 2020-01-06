package com.urraan.hamzakhan.ecommerece.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.urraan.hamzakhan.ecommerece.HomeActivity;
import com.urraan.hamzakhan.ecommerece.Models.ProductModel;
import com.urraan.hamzakhan.ecommerece.ProductsDetailActivity;
import com.urraan.hamzakhan.ecommerece.R;
import com.urraan.hamzakhan.ecommerece.ViewHolders.ProductViewHolder;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private DatabaseReference productReference;
    private RecyclerView recyclerMenu;
    private LinearLayoutManager layoutManager;
    private String type = "";



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        productReference = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerMenu = root.findViewById(R.id.recycler_menu);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerMenu.setHasFixedSize(true);
        recyclerMenu.setLayoutManager(layoutManager);




        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);

            }
        });
        return root;
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

                        Intent intent =  new Intent(getActivity(), ProductsDetailActivity.class);
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