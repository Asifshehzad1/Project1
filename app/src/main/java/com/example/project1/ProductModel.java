package com.example.project1;

public class ProductModel {
    private int id;
    private String name;
    private String description;
    private int stock;
    private double price;
    private String image;// Assuming this is a URL for remote images or a path for local images

    // Constructor for remote image URLs
    public ProductModel(int id, String name, int stock, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = this.description;
        this.stock = stock;
        this.price = price;
        this.image = image;
    }

    // Constructor for drawable resource IDs (if applicable)
    public ProductModel(int id, String name, String description, int stock, double price, int imageResourceId) {
        this(id, name, stock, price, String.valueOf(imageResourceId));
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
