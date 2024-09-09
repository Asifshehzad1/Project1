package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().hide();



        // Set up the drawer layout and toggle
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Set up the navigation view
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    // Handle profile action
                    Intent intent=new Intent(ProductActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                    return true;
                }if (id==R.id.nav_cart){
                    Intent intent=new Intent(ProductActivity.this,MycartActivity.class);
                    startActivity(intent);
                    return true;
                } if (id==R.id.nav_orderlist){
                    Intent intent=new Intent(ProductActivity.this,OrderListActivity.class);
                    startActivity(intent);
                }if (id==R.id.nav_next){
                    Intent intent=new Intent(ProductActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_logout) {
                    logout();
                    return true;
                }
                return false;
            }
        });

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
       GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
       recyclerView.setLayoutManager(gridLayoutManager);
      // recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch products
        fetchProducts();

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchProducts() {
        String token = AuthUtils.getToken(this);
        if (token != null && !token.isEmpty()) {
            UserClients userClients = ApiClient.getInstance().getUserClient();
            Call<List<ProductModel>> call = userClients.getProducts("Bearer " + token);
            call.enqueue(new Callback<List<ProductModel>>() {
                @Override
                public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ProductModel> productModels = response.body();
                        productAdapter = new ProductAdapter(ProductActivity.this, productModels);
                        recyclerView.setAdapter(productAdapter);
                        Toast.makeText(ProductActivity.this, "Fetched Data Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = "Error Code: " + response.code() + ", Message: " + response.message();
                        Toast.makeText(ProductActivity.this, "Data not found: " + errorMessage, Toast.LENGTH_LONG).show();
                        logout();
                    }
                }

                @Override
                public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                    Toast.makeText(ProductActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ProductActivity.this, "Token is missing or empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void logout() {
        AuthUtils.clearToken(this); // Clear the token

        // Optionally, navigate to the login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
