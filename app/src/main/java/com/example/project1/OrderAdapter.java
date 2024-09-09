package com.example.project1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderData> orders;
    private final Context context;

    public OrderAdapter(Context context, List<OrderData> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chechout_recycler, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderData order = orders.get(position);

        // Log the data for debugging
        Log.d("OrderAdapter", "Binding position " + position + ": Product Name: " + order.getProduct().getName() + ", Price: " + order.getTotal_price() + ", Payment Method: " + order.getPayment_method());

        // Set values to views
        holder.orderName.setText(order.getProduct().getName() != null ? order.getProduct().getName() : "N/A");
        holder.orderQuantity.setText(String.valueOf(order.getQuantity()));
        holder.orderPrice.setText(String.format("$%.2f", convertPrice(order.getTotal_price()))); // Convert and format the price
        holder.payment.setText(order.getPayment_method() != null ? order.getPayment_method() : "N/A");
        holder.address.setText(order.getAddress() != null ? order.getAddress() : "N/A");
        String imageUrl = order.getProduct().getImage();

        // Use Glide to handle image loading
        if (imageUrl != null) {
            try {
                int drawableResId = Integer.parseInt(imageUrl);
                Glide.with(context)
                        .load(drawableResId) // Load drawable resources
                        .into(holder.orderImage);
            } catch (NumberFormatException e) {
                Glide.with(context)
                        .load(imageUrl) // Load from URL
                        .error(R.drawable.person_24) // Optional error image
                        .into(holder.orderImage);
            }
        } else {
            holder.orderImage.setImageResource(R.drawable.images); // Placeholder image
        }

        // You can use Glide or Picasso to load the image into the ImageView if needed
        // Glide.with(context).load(order.getProduct().getImage()).into(holder.orderImage);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public void setOrders(List<OrderData> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    // Helper method to convert price from String to double
    private double convertPrice(String price) {
        try {
            return Double.parseDouble(price);
        } catch (NumberFormatException e) {
            Log.e("OrderAdapter", "Error parsing price: " + price, e);
            return 0.0;
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderName, orderPrice, orderQuantity, address, payment;
        ImageView orderImage;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.txt_order_name);
            orderPrice = itemView.findViewById(R.id.txt_orderPrice);
            orderQuantity = itemView.findViewById(R.id.txt_orderQuantity);
            address = itemView.findViewById(R.id.txt_orderAddress);
            payment = itemView.findViewById(R.id.txt_order_paymentMethod);
           orderImage = itemView.findViewById(R.id.img_order); // Ensure this ID matches your layout
        }
    }
}
