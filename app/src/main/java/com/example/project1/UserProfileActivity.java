package com.example.project1;

import static android.os.Build.VERSION_CODES.N;
import static android.os.Build.VERSION_CODES.S;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    TextView name,email,dob;
    ImageView imageEdit;
    Button delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name=findViewById(R.id.txt_display_name);
        email=findViewById(R.id.txt_display_email);
        dob=findViewById(R.id.txt_display_dob);
        imageEdit=findViewById(R.id.edit_image);



        fetchUserProfile();

        delete=findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(UserProfileActivity.this)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your Account")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //deleteAccount();
                                Toast.makeText(UserProfileActivity.this, "Delete Account", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
            }
        });

        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=name.getText().toString();
                String userEmail=email.getText().toString();
                String userDob=dob.getText().toString();

                Intent intent=new Intent(UserProfileActivity.this,UpdateActivity.class);
                intent.putExtra("USER_NAME", userName);
                intent.putExtra("USER_EMAIL", userEmail);
                intent.putExtra("USER_DOB", userDob);
                startActivity(intent);
                finish();
            }
        });

    }

//    private void fetchUserProfile(){
//        String token=AuthUtils.getToken(this);
//
//        if (token !=null && !token.isEmpty()){
//            UserClients userClients=ApiClient.getInstance().getUserClient();
//            Call<ResponseBody> call=userClients.getUserProfile("Bearer " + token);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.isSuccessful()){
//                        try {
//                            String stringResponseBody=response.body().string();
//                            Log.d("ApiResponse","Response body:"+stringResponseBody);
//
//                            JSONObject jsonObject=new JSONObject(stringResponseBody);
//                            boolean status=jsonObject.optBoolean("status",false);
//                            if (status){
//                                JSONObject userobject=jsonObject.optJSONObject("user");
//                                if (userobject!=null){
//                                    String username=jsonObject.optString("name","N/A");
//                                    String useremail=jsonObject.optString("email","N/A");
//                                    String userDob=jsonObject.optString("date_of_birth","N/A");
//
//                                    name.setText(""+ username);
//                                    email.setText(""+ useremail);
//                                    dob.setText(""+ userDob);
//                                }else {
//                                    Toast.makeText(UserProfileActivity.this, "User Data Not Found", Toast.LENGTH_SHORT).show();
//                                }
//                            }else {
//                                Toast.makeText(UserProfileActivity.this, "Invalid Status in Response", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (IOException | JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(UserProfileActivity.this, "Error Parsing Response", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Toast.makeText(UserProfileActivity.this, "Network Error"+t.getMessage(), Toast.LENGTH_SHORT).show();
//
//                }
//            });
//        }else {
//            Toast.makeText(this, "Token not Found", Toast.LENGTH_SHORT).show();
//        }
//    }
    private void fetchUserProfile() {
        String token = AuthUtils.getToken(this);

        if (token != null && !token.isEmpty()) {
            UserClients userClients = ApiClient.getInstance().getUserClient();
            Call<ResponseBody> call = userClients.getUserProfile("Bearer " + token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String responseBodyString = response.body().string();
                            Log.d("APIResponse", "Response body: " + responseBodyString);

                            JSONObject jsonObject = new JSONObject(responseBodyString);
                            boolean status = jsonObject.optBoolean("status", false);
                            if (status) {
                                JSONObject userObject = jsonObject.optJSONObject("user");
                                if (userObject != null) {
                                    String userName = userObject.optString("name", "N/A");
                                    String userEmail = userObject.optString("email", "N/A");
                                    String userDob = userObject.optString("date_of_birth", "N/A");

                                    name.setText("" + userName);
                                    email.setText("" + userEmail);
                                    dob.setText("" + userDob);
                                } else {
                                    Toast.makeText(UserProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(UserProfileActivity.this, "Invalid status in response", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(UserProfileActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            Log.e("ErrorResponse", "Error body: " + errorBody); // Log the error body
                            Toast.makeText(UserProfileActivity.this, "Error: " +  errorBody, Toast.LENGTH_SHORT).show();
                            AuthUtils.clearToken(UserProfileActivity.this);
                            Intent intent=new Intent(UserProfileActivity.this,LoginActivity.class);
                            startActivity(intent);

                        } catch (IOException e) {
                            Toast.makeText(UserProfileActivity.this, "Error catch: " +  e.getMessage(), Toast.LENGTH_SHORT).show();


                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(UserProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show();
        }
    }
//    private void deleteAccount() {
//        String token = AuthUtils.getToken(this);
//
//        if (token != null && !token.isEmpty()) {
//            UserClients userClients = ApiClient.getInstance().getUserClient();
//            Call<ResponseBody> call = userClients.deleteUserAccount("Bearer " + token);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.isSuccessful()) {
//                        Toast.makeText(UserProfileActivity.this, "Account successfully deleted", Toast.LENGTH_SHORT).show();
//                        AuthUtils.clearToken(UserProfileActivity.this);
//                        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        try {
//                            assert response.errorBody() != null;
//                            String errorBody = response.errorBody().string();
//                            Log.e("ErrorResponse", "Error body: " + errorBody);
//                            Toast.makeText(UserProfileActivity.this, "Error: " + errorBody, Toast.LENGTH_SHORT).show();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Toast.makeText(UserProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Toast.makeText(UserProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show();
//        }
//    }

}