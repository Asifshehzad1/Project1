package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private List<ProductModel> productModels;
    private Context context;

    // Constructor
    public ProductAdapter(Context context, List<ProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.recycler, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductModel product = productModels.get(position);
        String imageUrl = product.getImage();

        // Use Glide to handle image loading
        if (imageUrl != null) {
            try {
                int drawableResId = Integer.parseInt(imageUrl);
                Glide.with(context)
                        .load(drawableResId) // Load drawable resources
                        .into(holder.productImage);
            } catch (NumberFormatException e) {
                Glide.with(context)
                        .load(imageUrl) // Load from URL
                        .error(R.drawable.person_24) // Optional error image
                        .into(holder.productImage);
            }
        } else {
            holder.productImage.setImageResource(R.drawable.images); // Placeholder image
        }

        holder.name.setText(product.getName());
        holder.description.setText(product.getDescription());
        holder.stock.setText(String.valueOf(product.getStock()));
        holder.price.setText(String.format("$%.2f", product.getPrice()));

        holder.order.setOnClickListener(view -> {
            Intent intent = new Intent(context,OrderActivity.class);
            intent.putExtra("PRODUCT_NAME",product.getName());
            intent.putExtra("PRODUCT_IMAGE",product.getImage());
            intent.putExtra("PRODUCT_ID", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, price, stock;
        ImageView productImage;
        Button order;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.img_product);
            name = itemView.findViewById(R.id.product_name);
            description = itemView.findViewById(R.id.product_description);
            stock = itemView.findViewById(R.id.product_stock);
            price = itemView.findViewById(R.id.product_price);
            order = itemView.findViewById(R.id.btn_order);
        }
    }
}
