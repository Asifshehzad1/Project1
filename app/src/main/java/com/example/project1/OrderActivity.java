package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

public class OrderActivity extends AppCompatActivity {

    Button plus,minus;
    ImageView productImage;
    TextView txtquentity,productName,pid;
    int score=1;
    Button order,cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        productName=findViewById(R.id.txt_product_order);
        plus=findViewById(R.id.btn_plus);
        minus=findViewById(R.id.btn_mins);
        txtquentity=findViewById(R.id.txt_quentity);
        order=findViewById(R.id.btn_orderknow);
        cart=findViewById(R.id.btn_cart);
        productImage=findViewById(R.id.img_order);







        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score++;
                txtquentity.setText(String.valueOf(score));
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (score>1){
                    score--;
                    txtquentity.setText(String.valueOf(score));
                }
            }
        });

        Intent intent=getIntent();
        String productimage1=intent.getStringExtra("PRODUCT_IMAGE");
        String productName1=intent.getStringExtra("PRODUCT_NAME");
        int productId = intent.getIntExtra("PRODUCT_ID", -1);
        productName.setText(productName1);
        Glide.with(this)
                .load(productimage1)
                .placeholder(R.drawable.person_24) // Optional placeholder image
                .error(R.drawable.person_24) // Optional error image
                .into(productImage);




        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productName1 != null) {
                    CartManager.getInstance().addProduct(String.valueOf(productId), score);
                    Toast.makeText(OrderActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderActivity.this, "Product details missing", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
}