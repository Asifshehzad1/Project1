package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {
    TextView txtSighnup;
    EditText editemail,editPassword;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtSighnup=findViewById(R.id.txtsignup);
        editemail=findViewById(R.id.edit_loginEmail);
        editPassword=findViewById(R.id.edit_loginPassword);
        login=findViewById(R.id.btn_login);

        if (AuthUtils.getToken(this) != null) {
            // Token exists, navigate to DisplayProfileActivity
            findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            Intent intent = new Intent(LoginActivity.this, ProductActivity.class);
            startActivity(intent);
            finish();
            return; // Exit onCreate
        }


        txtSighnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

    }
    private void loginUser(){
        String email=editemail.getText().toString();
        String password=editPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            editemail.setError("Email is required");
            editemail.requestFocus();
        }if (TextUtils.isEmpty(password)){
            editPassword.setError("Required Password");
            editPassword.requestFocus();
        }if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editemail.setError("Invalid Email");
            editemail.requestFocus();
        }
        Call<ResponseBody> call =ApiClient.getInstance().getUserClient().loginUser(email,password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        String responsBodyString=response.body().string();
                        JSONObject jsonObject=new JSONObject(responsBodyString);
                        if (jsonObject.has("token")){
                            String token = jsonObject.getString("token");

                            AuthUtils.saveToken(LoginActivity.this, token);

                            Toast.makeText(LoginActivity.this, "Login Succesfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(LoginActivity.this,ProductActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "Error Reading Response", Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();

            }
        });

    }

}