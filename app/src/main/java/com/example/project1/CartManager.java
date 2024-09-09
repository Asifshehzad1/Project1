package com.example.project1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CartManager {
    private static CartManager instance;
    private final Map<String, Integer> cart;

    private CartManager() {
        cart = new HashMap<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProduct(String productId, int quantity) {
        cart.put(productId, quantity);
    }

    public Map<String, Integer> getCart() {
        return cart;
    }

    public void clearCart() {
        cart.clear();
    }

    public void updateCart(String productId, int quantity) {
        if (quantity <= 0) {
            cart.remove(productId);
        } else {
            cart.put(productId, quantity);
        }
    }

    public Order createOrder(String address, String paymentMethod) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            orderItems.add(new OrderItem(productId, quantity));
        }
        return new Order(orderItems, address, paymentMethod);
    }
}
