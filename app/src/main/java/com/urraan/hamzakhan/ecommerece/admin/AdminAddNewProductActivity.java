package com.urraan.hamzakhan.ecommerece.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.urraan.hamzakhan.ecommerece.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

  private String category, pname, price, description, saveCurrentDate, saveCurrentTime, productRandomId;
  private ImageView imageProduct;
  private Button btnAddProduct;
  private EditText inputProductname, inputProductPrice, inputProductDescription;
  private static final int GalleryPick = 1;
  private String downloadImageUrl;
  private DatabaseReference productDbReference;
  private Uri imageUri;
  private StorageReference ProductImageReference;
  private ProgressDialog loadingbar;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_add_new_product);
    category = getIntent().getStringExtra("category");
    ProductImageReference = FirebaseStorage.getInstance().getReference().child("Product Images");
    productDbReference = FirebaseDatabase.getInstance().getReference().child("Products");
    imageProduct = findViewById(R.id.select_product_image);
    btnAddProduct = findViewById(R.id.btn_add_product);
    inputProductname = findViewById(R.id.product_name);
    inputProductDescription = findViewById(R.id.product_description);
    inputProductPrice = findViewById(R.id.product_price);
    loadingbar = new ProgressDialog(this);
    imageProduct.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openGallery();
      }
    });
    btnAddProduct.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ValidateProduct();
      }
    });

  }

  private void ValidateProduct() {
    pname = inputProductname.getText().toString();
    price = inputProductPrice.getText().toString();
    description = inputProductDescription.getText().toString();
    if (imageUri == null) {
      Toast.makeText(this, "Product Image is mandatory", Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(pname)) {
      Toast.makeText(this, "Please enter product name", Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(description)) {
      Toast.makeText(this, "Please Enter product description", Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(price)) {
      Toast.makeText(this, "Please Enter product price", Toast.LENGTH_SHORT).show();
    } else {
      storeProductInformation();
    }
  }

  private void storeProductInformation() {
    loadingbar.setTitle("Adding Product");
    loadingbar.setMessage("Please wait while the Product is being added");
    loadingbar.setCanceledOnTouchOutside(false);
    loadingbar.show();
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
    saveCurrentDate = currentDate.format(calendar.getTime());
    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
    saveCurrentTime = currentTime.format(calendar.getTime());
    productRandomId = saveCurrentDate + saveCurrentTime;
    final StorageReference filePath = ProductImageReference.child(imageUri.getLastPathSegment() + productRandomId);
    final UploadTask uploadTask = filePath.putFile(imageUri);
    uploadTask.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Toast.makeText(AdminAddNewProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        loadingbar.dismiss();
      }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        Toast.makeText(AdminAddNewProductActivity.this, "Image added successfully", Toast.LENGTH_SHORT).show();
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
          @Override
          public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
            if (!task.isSuccessful()) {
              throw task.getException();
            }
            downloadImageUrl = filePath.getDownloadUrl().toString();
            return filePath.getDownloadUrl();
          }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
          @Override
          public void onComplete(@NonNull Task<Uri> task) {
            if (task.isSuccessful()) {
              downloadImageUrl = task.getResult().toString();
              Toast.makeText(AdminAddNewProductActivity.this, "Got product image url successfully", Toast.LENGTH_SHORT).show();
              saveProductToDatabase();
            }
          }
        });
      }
    });

  }

  private void saveProductToDatabase() {
    HashMap<String, Object> productMap = new HashMap<>();
    productMap.put("pid", productRandomId);
    productMap.put("date", saveCurrentDate);
    productMap.put("time", saveCurrentTime);
    productMap.put("description", description);
    productMap.put("image", downloadImageUrl);
    productMap.put("category", category);
    productMap.put("pname", pname);
    productMap.put("price", price);
    productDbReference.child(productRandomId).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
          loadingbar.dismiss();
          Toast.makeText(AdminAddNewProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
        } else {
          loadingbar.dismiss();
          String message = task.getException().toString();
          Toast.makeText(AdminAddNewProductActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void openGallery() {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_GET_CONTENT);
    intent.setType("image/*");
    startActivityForResult(intent, GalleryPick);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
      imageUri = data.getData();
      imageProduct.setImageURI(imageUri);
    }
  }
}