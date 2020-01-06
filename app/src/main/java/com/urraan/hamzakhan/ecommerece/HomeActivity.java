package com.urraan.hamzakhan.ecommerece;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.urraan.hamzakhan.ecommerece.Prevalent.Prevalent;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import android.view.Menu;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private DatabaseReference productReference;
    private AppBarConfiguration mAppBarConfiguration;
    private RecyclerView recyclerMenu;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);


        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        Paper.init(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(HomeActivity.this,CartActivity.class);
              startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView tvusername = headerView.findViewById(R.id.user_name_profile);
        CircleImageView userProfileImage = headerView.findViewById(R.id.user_profile_image);
        tvusername.setText(Prevalent.CurrentOnlineUser.getName());
        Picasso.get().load(Prevalent.CurrentOnlineUser.getImage()).placeholder(R.drawable.profile).into(userProfileImage);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_host_fragment,
                R.id.nav_home,
                R.id.nav_cart, R.id.nav_search, R.id.nav_categories,
                R.id.nav_settings, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.nav_logout) {
                    Paper.book().destroy();
                    Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } if (destination.getId() == R.id.nav_settings) {
                    Intent intent = new Intent(HomeActivity.this,SettingsActivity.class);
                    startActivity(intent);
                } if (destination.getId() == R.id.nav_cart) {
                    Intent intent = new Intent(HomeActivity.this,CartActivity.class);
                    startActivity(intent);
                } if (destination.getId() == R.id.nav_search) {
                    Intent intent = new Intent(HomeActivity.this,SearchProductsActivity.class);
                    startActivity(intent);
                }



            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
