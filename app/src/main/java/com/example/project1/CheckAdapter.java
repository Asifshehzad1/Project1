package com.example.project1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.CheckViewHolder>{
    private List<ProductModel> productModelList;
    private Context context;
    private CartAdapter.CartListener cartListener;
    private CartManager cartManager;

    public CheckAdapter(Context context, List<ProductModel> productModelList, CartAdapter.CartListener cartListener) {
        this.context = context;
        this.productModelList = productModelList != null ? productModelList : new ArrayList<>();
        this.cartListener = cartListener;
        this.cartManager = CartManager.getInstance();
    }


    @NonNull
    @Override
    public CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.chechout_recycler,parent,false);
        return new CheckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckViewHolder holder, int position) {
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
            holder.quntity.setText(String.format(" %d", quantity[0]));
        }


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

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public static class CheckViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name,price,quntity;

        public CheckViewHolder(@NonNull View itemView) {
            super(itemView);
//            name=itemView.findViewById(R.id.txt_checkCart_name);
//            price=itemView.findViewById(R.id.txtCheck_cartPrice);
//            quntity=itemView.findViewById(R.id.txtCheck_cartQuntity);
//            imageView=itemView.findViewById(R.id.img_checkcart);
        }
    }
}
