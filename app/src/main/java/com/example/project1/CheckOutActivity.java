package com.example.project1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutActivity extends AppCompatActivity implements CartAdapter.CartListener {
    TextView address,name,price,quntity;
    ImageView imageView;
    private RecyclerView recyclerView;
    private CheckAdapter checkAdapter;
    private List<ProductModel> cartItems;
    private TextView totalPriceTextView;
    private CartManager cartManager;
    CardView card;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView=findViewById(R.id.recycler_chech);
        address=findViewById(R.id.txt_address);
        imageView=findViewById(R.id.img_cart);
        name=findViewById(R.id.txt_cart_name);
        price=findViewById(R.id.txt_cartPrice);
        quntity=findViewById(R.id.txt_cartQuntity);
        totalPriceTextView=findViewById(R.id.txtaddress_totalPrice);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CheckOutActivity.this,AddressActivity.class);
                startActivity(intent);
            }
        });

        cartManager = CartManager.getInstance();

        Intent intent=getIntent();
        String productimage1=intent.getStringExtra("PRODUCT_IMAGE");
        String productName1=intent.getStringExtra("PRODUCT_NAME");

        cartItems = new ArrayList<>();
        checkAdapter = new CheckAdapter(this, cartItems, this);
        recyclerView.setAdapter(checkAdapter);

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

                    checkAdapter.notifyDataSetChanged();

                    // Update the total price TextView
                    totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));
                } else {
                    Toast.makeText(CheckOutActivity.this, "Error fetching cart data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(CheckOutActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTotalPriceUpdated(double totalPrice) {
        totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));
    }
}