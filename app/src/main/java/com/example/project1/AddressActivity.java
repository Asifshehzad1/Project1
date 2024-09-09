package com.example.project1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {
    private ImageView backArrow;
    private EditText name, phone, city, address, payment;
    private Button complete;
    private CartManager cartManager;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address);

        // Apply insets for edge-to-edge mode
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        // Initialize views
        name = findViewById(R.id.recip_name);
        phone = findViewById(R.id.recip_phone);
        city = findViewById(R.id.recip_city);
        address = findViewById(R.id.recip_street);
        payment = findViewById(R.id.recip_payment);
        complete = findViewById(R.id.btn_complete_order);
        backArrow = findViewById(R.id.back_arrow);

        cartManager = CartManager.getInstance();

        // Set onClick listeners
        backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(AddressActivity.this, MycartActivity.class);
            startActivity(intent);
            finish();
        });

        complete.setOnClickListener(view -> placeOrder());

        // Fetch user profile to pre-fill the form
        fetchUserProfile();
    }

    private boolean validateInfo(String recName, String recPhone, String recCity, String recAddress, String recPayment) {
        if (TextUtils.isEmpty(recName)) {
            name.setError("Please Input Name");
            name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(recCity)) {
            city.setError("Please Input City");
            city.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(recPhone)) {
            phone.setError("Please Input Phone");
            phone.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(recAddress)) {
            address.setError("Please Input Address");
            address.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(recPayment)) {
            payment.setError("Please Input Payment Method");
            payment.requestFocus();
            return false;
        }
        return true;
    }

    private void fetchUserProfile() {
        String token = AuthUtils.getToken(this);
        if (token != null && !token.isEmpty()) {
            UserClients userClients = ApiClient.getInstance().getUserClient();
            Call<ResponseBody> call = userClients.getUserProfile("Bearer " + token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String responseBodyString = response.body().string();
                            Log.d("APIResponse", "Response body: " + responseBodyString);
                            JSONObject jsonObject = new JSONObject(responseBodyString);
                            boolean status = jsonObject.optBoolean("status", false);
                            if (status) {
                                JSONObject userObject = jsonObject.optJSONObject("user");
                                if (userObject != null) {
                                    String userName = userObject.optString("name", "N/A");
                                    String userEmail = userObject.optString("email", "N/A");
                                    JSONObject addressObject = userObject.optJSONObject("address");
                                    if (addressObject != null) {
                                        String streetAddress = addressObject.optString("street", "N/A");
                                        String cityName = addressObject.optString("city", "N/A");
                                        city.setText(cityName);
                                        address.setText(streetAddress);
                                    } else {
                                        Toast.makeText(AddressActivity.this, "Address data not found", Toast.LENGTH_SHORT).show();
                                    }
                                    name.setText(userName);
                                    phone.setText(userEmail);
                                } else {
                                    Toast.makeText(AddressActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AddressActivity.this, "Invalid status in response", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddressActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            Log.e("ErrorResponse", "Error body: " + errorBody);
                            Toast.makeText(AddressActivity.this, "Error: " + errorBody, Toast.LENGTH_SHORT).show();
                            AuthUtils.clearToken(AddressActivity.this);
                            Intent intent = new Intent(AddressActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } catch (IOException e) {
                            Toast.makeText(AddressActivity.this, "Error catch: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(AddressActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show();
        }
    }

    private void placeOrder() {
        String rName = name.getText().toString().trim();
        String rCity = city.getText().toString().trim();
        String rPhone = phone.getText().toString().trim();
        String rAddress = address.getText().toString().trim();
        String rPayment = payment.getText().toString().trim();

        if (!validateInfo(rName, rPhone, rCity, rAddress, rPayment)) return;

        // Create the order with address and payment method
        Order order = cartManager.createOrder(rAddress, rPayment);
        String token = AuthUtils.getToken(this);
        UserClients userClients = ApiClient.getInstance().getUserClient();

        // Send each item in the order
        for (OrderItem item : order.getItems()) {
            Log.d("Order", "Product ID: " + item.getProductId());
            Log.d("Order", "Quantity: " + item.getQuantity());

            Call<Void> call = userClients.placeOrder("Bearer " + token,
                    item.getProductId(),
                    String.valueOf(item.getQuantity()),
                    order.getPaymentMethod(),
                    order.getAddress()
            );

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        cartManager.clearCart(); // Clear the cart after successful order
                        Toast.makeText(AddressActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                        // Optionally, redirect the user to another activity or update UI
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("APIError", "Error body: " + errorBody);
                            Toast.makeText(AddressActivity.this, "Failed to place order: " + response.code(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(AddressActivity.this, "Error reading error body", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddressActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
