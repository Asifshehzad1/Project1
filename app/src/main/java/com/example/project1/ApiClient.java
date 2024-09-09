package com.example.project1;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL="https://jwtauth.techxdeveloper.com/api/";
    public static ApiClient mInstance;
    public Retrofit retrofit;

    private ApiClient(){
        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
public static synchronized ApiClient getInstance(){
        if (mInstance==null){
            mInstance=new ApiClient();
        } return mInstance;
}
public UserClients getUserClient(){
        return retrofit.create(UserClients.class);
}

}
