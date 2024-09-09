package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<OrderData> orders = new ArrayList<>();
    ImageView arrowBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        getSupportActionBar().hide();

        arrowBack = findViewById(R.id.bArrow_OrderList);
        arrowBack.setOnClickListener(view -> {
            Intent intent = new Intent(OrderListActivity.this, ProductActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerView = findViewById(R.id.recycler_order); // Ensure this ID matches your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, orders);
        recyclerView.setAdapter(adapter);

        fetchOrders();
    }

    private void fetchOrders() {
        String token = AuthUtils.getToken(this);
        if (token != null && !token.isEmpty()) {
            UserClients userClients = ApiClient.getInstance().getUserClient();
            Call<List<OrderData>> call = userClients.getOrders("Bearer " + token);
            call.enqueue(new Callback<List<OrderData>>() {
                @Override
                public void onResponse(Call<List<OrderData>> call, Response<List<OrderData>> response) {
                    if (response.isSuccessful()) {
                        orders = response.body(); // Update orders here
                        if (orders != null) {
                            adapter.setOrders(orders); // Update the adapter
                        } else {
                            Toast.makeText(OrderListActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(OrderListActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<OrderData>> call, Throwable t) {
                    Toast.makeText(OrderListActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show();
        }
    }
}
