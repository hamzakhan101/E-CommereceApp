package com.urraan.hamzakhan.ecommerece.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.urraan.hamzakhan.ecommerece.R;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AdminMaintainProductActivity extends AppCompatActivity {

    private EditText etName,etPrice,etDescription;
    private Button btnUpdateProduct,btnDeleteProduct;
    private ImageView imageView;
    private String productID = "";
    private DatabaseReference productsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);
        imageView = findViewById(R.id.maintain_product_image);
        btnUpdateProduct = findViewById(R.id.update_product);
        btnDeleteProduct = findViewById(R.id.delete_product_btn);
        etName = findViewById(R.id.maintain_product_name);
        etPrice = findViewById(R.id.maintain_product_price);
        etDescription = findViewById(R.id.maintain_product_description);
        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        displayProductInfo();
        btnUpdateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduct();
            }
        });
        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteThisProduct();
            }
        });
    }

    private void DeleteThisProduct() {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminMaintainProductActivity.this, "Product Removed Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminMaintainProductActivity.this, AdminCategoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateProduct() {
        String name = etName.getText().toString();
        String price = etPrice.getText().toString();
        String description = etDescription.getText().toString();
        if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please Enter All fields", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description", description);
            productMap.put("pname", name);
            productMap.put("price", price);
            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminMaintainProductActivity.this,
                                "Product Updated Successfully ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminMaintainProductActivity.this,AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displayProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("pname").getValue().toString();
                    String price = dataSnapshot.child("price").getValue().toString();
                    String desc = dataSnapshot.child("description").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();

                    etName.setText(name);
                    etPrice.setText(price);
                    etDescription.setText(desc);
                    Picasso.get().load(image).into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
