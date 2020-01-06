package com.urraan.hamzakhan.ecommerece;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.urraan.hamzakhan.ecommerece.Models.AdminOrdersModel;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrdersModel> options =
                new FirebaseRecyclerOptions.Builder<AdminOrdersModel>()
                .setQuery(ordersRef,AdminOrdersModel.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrdersModel,AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrdersModel, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrderViewHolder adminOrderViewHolder, final int i, @NonNull final AdminOrdersModel adminOrdersModel) {

                        adminOrderViewHolder.tvUsername.setText("Name: " + adminOrdersModel.getName());
                        adminOrderViewHolder.tvUserphone.setText("Phone: " + adminOrdersModel.getPhone());
                        adminOrderViewHolder.tvUserAddress.setText("Address: " + adminOrdersModel.getAddress() + adminOrdersModel.getCity());
                        adminOrderViewHolder.tvDateTime.setText("Ordered at: " + adminOrdersModel.getDate() + "Time: " + adminOrdersModel.getTime());
                        adminOrderViewHolder.tvTotalPrice.setText("Total Price: $" + adminOrdersModel.getTotalAmount());
                        adminOrderViewHolder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uid = getRef(i).getKey();
                                Intent intent = new Intent(AdminNewOrdersActivity.this,AdminDisplayProductsActivity.class);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        });
                        adminOrderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                    "Yes",
                                        "No"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Have you shipped the products?")
                                        .setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                           if (i == 0) {
                                               String uid = getRef(i).getKey();
                                               RemoveOrder(uid);
                                           } else {
                                               finish();
                                           }
                                            }
                                        });
                                builder.show();
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items_layout,parent,false);
                        return new AdminOrderViewHolder(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder{

        TextView tvUsername,tvUserphone,tvUserAddress,tvTotalPrice,tvDateTime;
        Button showOrdersBtn;
        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.order_user_name);
            tvUserphone = itemView.findViewById(R.id.order_phone_number);
            tvUserAddress = itemView.findViewById(R.id.order_address);
            tvTotalPrice = itemView.findViewById(R.id.order_total_price);
            tvDateTime = itemView.findViewById(R.id.order_date_time);
            showOrdersBtn = itemView.findViewById(R.id.show_ordered_products);

        }
    }
    private void RemoveOrder(String uid) {
        ordersRef.child(uid).removeValue();
    }

}
