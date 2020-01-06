package com.urraan.hamzakhan.ecommerece;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.urraan.hamzakhan.ecommerece.Models.Users;
import com.urraan.hamzakhan.ecommerece.Prevalent.Prevalent;

public class MainActivity extends AppCompatActivity {

    Button btnlogin, btnjoin;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnlogin = findViewById(R.id.main_btn_login_btn);
        btnjoin = findViewById(R.id.main_join_now_btn);
        loadingbar = new ProgressDialog(this);
        Paper.init(this);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhonekey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        if (UserPasswordKey != "" && UserPhoneKey != ""){
            if (!TextUtils.isEmpty(UserPasswordKey) && !TextUtils.isEmpty(UserPhoneKey)) {
                AllowAccess(UserPhoneKey,UserPasswordKey);
                loadingbar.setTitle("Login Account");
                loadingbar.setMessage("Please wait...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
            }
        }

    }

    private void AllowAccess(final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()) {
                    Users usersdata = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (phone.equals(usersdata.getPhone())) {
                        if (usersdata.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.CurrentOnlineUser = usersdata;
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter valid phone", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "User with this " + phone + " doesn't exist", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(MainActivity.this, "You need to create an account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
