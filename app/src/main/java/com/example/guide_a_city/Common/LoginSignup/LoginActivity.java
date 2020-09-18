package com.example.guide_a_city.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.guide_a_city.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {


    CountryCodePicker countryCodePicker;
    TextInputLayout phoneNumber, password;
    RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        countryCodePicker = findViewById(R.id.login_countrycodepicker);
        phoneNumber = findViewById(R.id.login_phoneNumber);
        password = findViewById(R.id.login_pass);
        progressBar = findViewById(R.id.login_progressbar);
    }

    public void userLogin(View view) {

        if (!validateFields()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //get data

        String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
        final String _password = password.getEditText().getText().toString().trim();

        if (_phoneNumber.charAt(0) == '0') {
            _phoneNumber = _phoneNumber.substring(1);
        }
        final String _completephoneNumber = "+" + countryCodePicker.getFullNumber() + _phoneNumber;

        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(_completephoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phoneNumber.setError(null);
                    phoneNumber.setErrorEnabled(false);

                    String SystemPasswor = snapshot.child(_completephoneNumber).child("password").getValue(String.class);
                    if (SystemPasswor.equals(_password)) {
                        password.setError(null);
                        phoneNumber.setErrorEnabled(false);

                        String _fullname = snapshot.child(_completephoneNumber).child("fullname").getValue(String.class);
                        String _email = snapshot.child(_completephoneNumber).child("email").getValue(String.class);
                        String _dateofbirth = snapshot.child(_completephoneNumber).child("date").getValue(String.class);
                        String _username = snapshot.child(_completephoneNumber).child("username").getValue(String.class);


                        Toast.makeText(LoginActivity.this, _fullname + "\n" + _email + "\n" + _dateofbirth + "\n" + _username, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "No such User exist", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean validateFields() {

        String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();

        if (_phoneNumber.isEmpty()) {
            phoneNumber.setError("Phone Number can not be empty");
            phoneNumber.requestFocus();
            return false;

        } else if (_password.isEmpty()) {
            password.setError("Password can not be empty");
            password.requestFocus();
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }

    }
}