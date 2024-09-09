package com.example.project1;



import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserClients {
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> registerUsers(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("date_of_birth") String dob // Ensure this matches the server's expected field name
    );
    @GET("check_token")
    Call<ResponseBody> getUserProfile(@Header("Authorization") String authHeader);
    @GET("products")
    Call<List<ProductModel>> getProducts(@Header("Authorization") String authHeader);
    @POST("user/update")
    @FormUrlEncoded
    Call<ResponseBody> updateUser(
            @Header("authorization") String token,
            @Field("name") String name,
            @Field("email") String email,
            @Field("date_of_birth") String dob
    );

    @GET("products-bulk?ids=2,3")
    Call<List<ProductModel>> addItemToCart(@Query("ids") String ids, @Header("Authorization") String authHeader);
    @POST("orders")
    @FormUrlEncoded
    Call<Void> placeOrder(
            @Header("authorization") String token,
            @Field("product_id") String id,
            @Field("quantity") String quantity,
            @Field("payment_method") String paymentMethod,
            @Field("address") String address
    );
    @GET("orders")
    Call<List<OrderData>> getOrders(@Header("Authorization") String token);
}
