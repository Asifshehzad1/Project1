package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MycartActivity extends AppCompatActivity implements CartAdapter.CartListener {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<ProductModel> cartItems;
    private TextView totalPriceTextView;
    private CartManager cartManager;
    private Button order;
    CardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycart);

        recyclerView = findViewById(R.id.cart_recyclerview1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        totalPriceTextView = findViewById(R.id.txt_totalPrice);
        order = findViewById(R.id.btn_place_order);
        card=findViewById(R.id.card_cart);

        cartManager = CartManager.getInstance(); // Initialize CartManager

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(MycartActivity.this,AddressActivity.class);
               startActivity(intent);
            }
        });

        // Initialize cartItems list
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItems, this);
        recyclerView.setAdapter(cartAdapter);

        // Fetch cart data
        fetchCartData();
    }

    private void fetchCartData() {
        String token = AuthUtils.getToken(this);
        Map<String, Integer> cart = cartManager.getCart();

        // Handle empty cart
        if (cart.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            card.setVisibility(View.GONE);
            return;
        }

        // Convert map keys to comma-separated string of IDs
        String ids = String.join(",", cart.keySet());

        UserClients userClients = ApiClient.getInstance().getUserClient();
        Call<List<ProductModel>> call = userClients.addItemToCart(ids, "Bearer " + token); // Fixed method name

        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> products = response.body();
                    cartItems.clear();
                    double totalPrice = 0.0;

                    for (ProductModel product : products) {
                        int quantity = cart.get(String.valueOf(product.getId()));
                        product.setStock(quantity);
                        cartItems.add(product);
                        totalPrice += product.getPrice() * quantity;
                    }

                    cartAdapter.notifyDataSetChanged();

                    // Update the total price TextView
                    totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));
                } else {
                    Toast.makeText(MycartActivity.this, "Error fetching cart data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(MycartActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTotalPriceUpdated(double totalPrice) {
        totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));
    }
//
//    private void placeOrder() {
//        Order order = cartManager.createOrder(); // Create the order
//        String token = AuthUtils.getToken(this);
//        UserClients userClients = ApiClient.getInstance().getUserClient();
//        Call<Void> call = userClients.placeOrder(order, "Bearer " + token); // Fixed method name
//
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    cartManager.clearCart(); // Clear the cart after successful order
//                    Toast.makeText(MycartActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
//                    // Optionally, redirect the user to another activity or update UI
//                } else {
//                    Toast.makeText(MycartActivity.this, "Failed to place order" + response.code(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(MycartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
