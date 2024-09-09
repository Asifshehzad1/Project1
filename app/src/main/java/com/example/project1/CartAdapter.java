package com.example.project1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<ProductModel> productModelList;
    private Context context;
    private CartListener cartListener;
    private CartManager cartManager;

    public CartAdapter(Context context, List<ProductModel> productModelList, CartListener cartListener) {
        this.context = context;
        this.productModelList = productModelList != null ? productModelList : new ArrayList<>();
        this.cartListener = cartListener;
        this.cartManager = CartManager.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_recycler, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel cartModel = productModelList.get(position);
        if (cartModel != null) {
            String name = cartModel.getName();
            String imageUrl = cartModel.getImage();
            final int[] quantity = {cartModel.getStock()};
            double price = cartModel.getPrice();

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.person_24)
                    .error(R.drawable.image2)
                    .into(holder.imageView);

            holder.name.setText(name);
            holder.price.setText(String.format("$%.2f", price));
            holder.quantity.setText(String.format(" %d", quantity[0]));

            holder.qPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // if (quantity[0] < cartModel.getStock()) { // Check for max stock limit
                        quantity[0]++;
                        cartModel.setStock(quantity[0]);
                        holder.quantity.setText(String.format(" %d", quantity[0]));
                        cartManager.updateCart(String.valueOf(cartModel.getId()), quantity[0]);
                        updateTotalPrice();
                   // }
                }
            });

            holder.qMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (quantity[0] > 1) {
                        quantity[0]--;
                        cartModel.setStock(quantity[0]);
                        holder.quantity.setText(String.format(" %d", quantity[0]));
                        cartManager.updateCart(String.valueOf(cartModel.getId()), quantity[0]);
                        updateTotalPrice();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    private void updateTotalPrice() {
        double totalPrice = 0.0;
        for (ProductModel product : productModelList) {
            totalPrice += product.getPrice() * product.getStock();
        }
        if (cartListener != null) {
            cartListener.onTotalPriceUpdated(totalPrice);
        }
    }

    public interface CartListener {
        void onTotalPriceUpdated(double totalPrice);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, price, quantity;
        Button qPlus, qMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_cart);
            name = itemView.findViewById(R.id.txt_cart_name);
            price = itemView.findViewById(R.id.txt_cartPrice);
            quantity = itemView.findViewById(R.id.txt_quentity);
            qPlus = itemView.findViewById(R.id.btn_qplus);
            qMinus = itemView.findViewById(R.id.btn_qmins);
        }
    }
}
