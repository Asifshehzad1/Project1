package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateActivity extends AppCompatActivity {


    TextView name,email,dob;
    ImageView updateImage,backArrow;
    Button update;
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().hide();

        name=findViewById(R.id.update_name);
        email=findViewById(R.id.update_email);
        dob=findViewById(R.id.update_dob);
        update=findViewById(R.id.btn_update);


        backArrow=findViewById(R.id.bArrow_my_account);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UpdateActivity.this,UserProfileActivity.class);
                startActivity(intent);
            }
        });


        userToken= AuthUtils.getToken(this);


        Intent intent=getIntent();
        String userName=intent.getStringExtra("USER_NAME");
        String userEmail=intent.getStringExtra("USER_EMAIL");
        String userDob=intent.getStringExtra("USER_DOB");

        name.setText(userName);
        email.setText(userEmail);
        dob.setText(userDob);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });




    }

    public void updateUser(){
        String name1=name.getText().toString().trim();
        String email1=email.getText().toString().trim();
        String dob1=dob.getText().toString().trim();

        if (TextUtils.isEmpty(name1)){
           name.setError("Name is Required ");
           name.requestFocus();
        }
        if (TextUtils.isEmpty(email1)){
            email.setError("Email is Required");
            email.requestFocus();
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
            name.setError("Enter Valid Email");
            name.requestFocus();
        }
        if (TextUtils.isEmpty(dob1)){
            dob.setError("Date of Birth id Required");
            dob.requestFocus();
        }

        Call<ResponseBody> cal=ApiClient.getInstance().getUserClient().updateUser("Bearer" + userToken,name1,email1,dob1);
        cal.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(UpdateActivity.this, "Update SuccessFully", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(UpdateActivity.this,ProductActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(UpdateActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UpdateActivity.this, "Network Error"+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}