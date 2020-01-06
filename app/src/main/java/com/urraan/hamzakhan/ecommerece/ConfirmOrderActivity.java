package com.urraan.hamzakhan.ecommerece;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.urraan.hamzakhan.ecommerece.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {

    EditText etName,etPhone,etAddress,etCity;
    Button confirmOrderBtn;
    private String totalAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        etName = findViewById(R.id.shipment_name);
        etPhone = findViewById(R.id.shipment_phone);
        etAddress = findViewById(R.id.shipment_address);
        etCity = findViewById(R.id.shipment_city);
        confirmOrderBtn = findViewById(R.id.confirm_order_btn);
        totalAmount = getIntent().getStringExtra("TotalPrice");
        Toast.makeText(this, totalAmount, Toast.LENGTH_SHORT).show();
        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });
    }

    private void confirmOrder() {
        String name,phone,address,city;
        name = etName.getText().toString();
        phone = etPhone.getText().toString();
        address = etAddress.getText().toString();
        city = etCity.getText().toString();
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please Enter All fields", Toast.LENGTH_SHORT).show();
        } else {
           final String saveCurrentTime,saveCurrentDate;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM,dd,yyyy");
            saveCurrentDate = dateFormat.format(calendar.getTime());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = timeFormat.format(calendar.getTime());
            final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                    .child(Prevalent.CurrentOnlineUser.getPhone());
            HashMap<String,Object> ordersMap = new HashMap<>();
            ordersMap.put("totalAmount",totalAmount);
            ordersMap.put("name",name);
            ordersMap.put("phone",phone);
            ordersMap.put("address",address);
            ordersMap.put("city",city);
            ordersMap.put("time",saveCurrentTime);
            ordersMap.put("date",saveCurrentDate);
            ordersMap.put("state","not shipped");
            ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("Cart List")
                                .child("User View")
                                .child(Prevalent.CurrentOnlineUser.getPhone())
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ConfirmOrderActivity.this, "Your Order has been placed successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ConfirmOrderActivity.this,HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                    }
                }
            });
        }
    }
}
