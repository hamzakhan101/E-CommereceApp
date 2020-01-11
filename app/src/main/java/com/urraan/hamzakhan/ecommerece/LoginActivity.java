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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.urraan.hamzakhan.ecommerece.Models.Users;
import com.urraan.hamzakhan.ecommerece.Prevalent.Prevalent;
import com.urraan.hamzakhan.ecommerece.admin.AdminCategoryActivity;

public class LoginActivity extends AppCompatActivity {


    private EditText inputphone,inputpassword;
    private Button btnLogin;
    private ProgressDialog loadingbar;
    private String ParentDbname = "Users";
    private CheckBox chkboxRememberMe;
    private TextView adminLink,notAdminLink,forgetPasswrodLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputphone = findViewById(R.id.login_phone_input);
        inputpassword = findViewById(R.id.login_password_input);
        btnLogin = findViewById(R.id.login_btn);
        loadingbar = new ProgressDialog(this);
        chkboxRememberMe = findViewById(R.id.remember_me_chk);
        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.not_an_admin_link);
        forgetPasswrodLink = findViewById(R.id.forget_password);
        forgetPasswrodLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });
        Paper.init(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginuser();
            }
        });
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setText("Login as Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                ParentDbname = "Admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnLogin.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                ParentDbname = "Users";
            }
        });
    }

    private void loginuser() {
        String phone = inputphone.getText().toString();
        String password = inputpassword.getText().toString();
         if (TextUtils.isEmpty(phone)) {
            inputphone.setError("please enter your phone number");
        }
        else if (TextUtils.isEmpty(password)) {
            inputpassword.setError("please enter your password");
        } else {
             loadingbar.setTitle("Login Account");
             loadingbar.setMessage("Please wait while we are checking your credentials");
             loadingbar.setCanceledOnTouchOutside(false);
             loadingbar.show();
             AllowAccessToAccount(phone,password);
         }
    }

    private void AllowAccessToAccount(final String phone, final String password) {
        if (chkboxRememberMe.isChecked()) {
            Paper.book().write(Prevalent.UserPhonekey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(ParentDbname).child(phone).exists()) {
                    Users usersdata = dataSnapshot.child(ParentDbname).child(phone).getValue(Users.class);
                    if (usersdata.getPhone().equals(phone)) {
                        if (usersdata.getPassword().equals(password)) {
                            if (ParentDbname.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            } else if (ParentDbname.equals("Users")) {
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.CurrentOnlineUser = usersdata;
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "User with this " + phone + " doesn't exist", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(LoginActivity.this, "You need to create an account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
