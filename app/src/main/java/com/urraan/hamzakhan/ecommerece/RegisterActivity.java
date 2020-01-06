package com.urraan.hamzakhan.ecommerece;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button btncreateaccount;
    private EditText inputname,inputnumber,inputpassword;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btncreateaccount = findViewById(R.id.create_account_btn);
        inputname = findViewById(R.id.register_username_input);
        inputnumber = findViewById(R.id.register_phone_input);
        inputpassword = findViewById(R.id.register_password_input);
        loadingbar = new ProgressDialog(this);
        btncreateaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String name = inputname.getText().toString();
        String phone = inputnumber.getText().toString();
        String password = inputpassword.getText().toString();
        if (TextUtils.isEmpty(name)) {
            inputname.setError("Please enter your name");
        }
        else if (TextUtils.isEmpty(phone)) {
            inputnumber.setError("please enter your phone number");
        }
        else if (TextUtils.isEmpty(password)) {
            inputpassword.setError("please enter your password");
        } else {
            loadingbar.setTitle("Creating Account");
            loadingbar.setMessage("Please wait while we are checking your credentials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            validatephonenumber(name,phone,password);
        }
    }

    private void validatephonenumber(final String name, final String phone, final String password) {
        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Users").child(phone).exists()) {
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);
                    Rootref.child("Users").child(phone).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(RegisterActivity.this, "You are registered successfuly", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                            } else {
                                loadingbar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Network problem! please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "this " + phone + " already exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Try again using another phone number", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
