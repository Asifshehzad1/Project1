package com.example.project1;

import android.app.DatePickerDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    TextView txtSignup;
    EditText editName, editEmail, editPassword, editDob;
    Button register;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        initializeViews();
        setOnClickListeners();
    }

    private void initializeViews() {
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editDob = findViewById(R.id.edit_dob);
        txtSignup = findViewById(R.id.txt_login);
        register = findViewById(R.id.btn_register);
        calendar = Calendar.getInstance();
    }

    private void setOnClickListeners() {
        txtSignup.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        editDob.setOnClickListener(view -> showDatePickerDialog());

        register.setOnClickListener(view -> userSignUp());
    }

    private void userSignUp() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String dob = editDob.getText().toString().trim();

        if (!validateInput(name, email, password, dob)) return;

        Call<ResponseBody> call = ApiClient.getInstance().getUserClient().registerUsers(name, email, password, dob);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    handleSuccessfulResponse(response);
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String name, String email, String password, String dob) {
        if (TextUtils.isEmpty(name)) {
            editName.setError("Enter name please");
            editName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Enter Email Please");
            editEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter Valid Email");
            editEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Please Enter Password");
            editPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            editPassword.setError("Password must be at least 6 characters");
            editPassword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(dob)) {
            editDob.setError("Date of birth is required");
            editDob.requestFocus();
            return false;
        }
//        if (!isAgeValid(dob)) {
//            editDob.setError("You must be at least 18 years old");
//            editDob.requestFocus();
//            return false;
//        }
        return true;
    }

    private void handleSuccessfulResponse(Response<ResponseBody> response) {
        try {
            String responseBodyString = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBodyString);

            clearErrors();

            if (jsonObject.has("errors")) {
                handleErrors(jsonObject.getJSONObject("errors"));
            } else if (jsonObject.has("token")) {
                Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            } else {
                Toast.makeText(RegisterActivity.this, "Unexpected Response Format: " + responseBodyString, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterActivity.this, "Error reading Response", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleErrors(JSONObject errors) throws JSONException {
        if (errors.has("email")) {
            JSONArray emailErrors = errors.getJSONArray("email");
            editEmail.setError(formatErrorMessages(emailErrors));
        }
        if (errors.has("password")) {
            JSONArray passwordErrors = errors.getJSONArray("password");
            editPassword.setError(formatErrorMessages(passwordErrors));
        }
        if (errors.has("name")) {
            JSONArray nameErrors = errors.getJSONArray("name");
            editName.setError(formatErrorMessages(nameErrors));
        }
        if (errors.has("date of birth")) {
            JSONArray dobErrors = errors.getJSONArray("date of birth");
            editDob.setError(formatErrorMessages(dobErrors));
        }
    }

    private String formatErrorMessages(JSONArray errorArray) throws JSONException {
        StringBuilder errorMessage = new StringBuilder();
        for (int i = 0; i < errorArray.length(); i++) {
            errorMessage.append(errorArray.getString(i)).append("\n");
        }
        return errorMessage.toString().trim();
    }

    private void handleError(Response<ResponseBody> response) {
        String errorMessage = "Error Code: " + response.code() + ", Message: " + response.message();
        Toast.makeText(RegisterActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    private void clearErrors() {
        editEmail.setError(null);
        editName.setError(null);
        editPassword.setError(null);
        editDob.setError(null);
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> updateDate(year1, month1, dayOfMonth),
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void updateDate(int year, int month, int day) {
        calendar.set(year, month, day);
        String formattedDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(calendar.getTime());
        editDob.setText(formattedDate);
    }

//    private boolean isAgeValid(String dob) {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
//            Calendar dobCalendar = Calendar.getInstance();
//            dobCalendar.setTime(sdf.parse(dob));
//
//            Calendar today = Calendar.getInstance();
//            int age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
//            if (dobCalendar.get(Calendar.MONTH) > today.get(Calendar.MONTH)
//                    || (dobCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
//                    dobCalendar.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
//                age--;
//            }
//            return age >= 18;
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
