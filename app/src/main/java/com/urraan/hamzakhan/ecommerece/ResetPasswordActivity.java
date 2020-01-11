package com.urraan.hamzakhan.ecommerece;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.urraan.hamzakhan.ecommerece.Prevalent.Prevalent;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check = "";
    private TextView tvResetPass,tvQuestionTitle;
    private EditText etPhone,etQuestion1,etQuestion2;
    private Button verifyBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        check = getIntent().getStringExtra("check");
        tvResetPass = findViewById(R.id.tv_reset_pass);
        tvQuestionTitle = findViewById(R.id.security_questions_title);
        etPhone = findViewById(R.id.find_phone_number);
        etQuestion1 = findViewById(R.id.question_1);
        etQuestion2 = findViewById(R.id.question_2);
        verifyBtn = findViewById(R.id.verify_btn);


    }

    @Override
    protected void onStart() {
        super.onStart();
        etPhone.setVisibility(View.GONE);

        if (check.equals("settings")) {
            DisplayPreviousAnswers();
            tvResetPass.setText("Set Security Questions");
            tvQuestionTitle.setText("Set your Security Questions");
            verifyBtn.setText("Save");

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String answer1 = etQuestion1.getText().toString().toLowerCase();
                    String answer2 = etQuestion2.getText().toString().toLowerCase();
                    if (answer1.isEmpty() || answer2.isEmpty()) {
                        Toast.makeText(ResetPasswordActivity.this, "Please Answer Both questions", Toast.LENGTH_SHORT).show();
                    } else {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(Prevalent.CurrentOnlineUser.getPhone());
                        HashMap<String,Object> answerMap = new HashMap<>();
                        answerMap.put("answer1",answer1);
                        answerMap.put("answer2",answer2);

                        reference.child("Security Questions").updateChildren(answerMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ResetPasswordActivity.this, "Your Response was saved for the Security Questions", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ResetPasswordActivity.this,HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                }
            });


        }
        if (check.equals("login")) {
            etPhone.setVisibility(View.VISIBLE);
            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();
                }
            });
        }
    }

    private void verifyUser() {
        final String phone = etPhone.getText().toString();
        final String answer1 = etQuestion1.getText().toString().toLowerCase();
        final String answer2 = etQuestion2.getText().toString().toLowerCase();
        if (!phone.isEmpty() || !answer1.isEmpty() || !answer2.isEmpty()) {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(phone);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String otherPhone = dataSnapshot.child("phone").getValue().toString();
                        if (dataSnapshot.hasChild("Security Questions")) {
                            String savedAnswer1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String savedAnswer2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();
                            if (!answer1.equals(savedAnswer1)) {
                                etQuestion1.setError("Wrong Answer");
                                etQuestion1.requestFocus();
                            } else if (!answer2.equals(savedAnswer2)) {
                                etQuestion2.setError("Wrong Answer");
                                etQuestion2.requestFocus();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");
                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Enter your new Password");

                                builder.setView(newPassword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        if (!newPassword.getText().toString().equals("")) {
                                            reference.child("password").setValue(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ResetPasswordActivity.this, "Password has been changed successfully", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(ResetPasswordActivity.this,LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        }
                        else {
                            Toast.makeText(ResetPasswordActivity.this, "U have not set the security questions", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "User with this Phone Number Doesn't exist", Toast.LENGTH_SHORT).show();
                        etPhone.setError("InValid Number");
                        etPhone.requestFocus();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Please Enter the required information", Toast.LENGTH_SHORT).show();
        }

    }

    private void DisplayPreviousAnswers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Prevalent.CurrentOnlineUser.getPhone());
        reference.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String answer1 = dataSnapshot.child("answer1").getValue().toString();
                    String answer2 = dataSnapshot.child("answer2").getValue().toString();
                    etQuestion1.setText(answer1);
                    etQuestion2.setText(answer2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
