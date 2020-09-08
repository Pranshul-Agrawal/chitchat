package com.example.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText registerEmail, registerPassword;
    private FirebaseAuth userAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        userAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    public void createAccount(View view) {
        final String email = registerEmail.getText().toString();
        final String password = registerPassword.getText().toString();

        progressDialog.setTitle("Creating new user");
        progressDialog.setMessage("Signing up a new user please wait.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (TextUtils.isEmpty(email)) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please enter a valid email Address", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password) || isinValidPassword(password)) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please enter a valid Password", Toast.LENGTH_SHORT).show();

        } else {
            userAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                Intent MainActivityIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(MainActivityIntent);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "some internal error occured", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    public void alreadyHaveAccount(View view) {
        Intent loginActivityIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginActivityIntent);
    }

    /*    User Defined Methods   */

    private boolean isinValidPassword(String password) {
        /*
         **^                         Start anchor
         **(?=.*[A-Z].*[A-Z])        Ensure string has two uppercase letters.
         **(?=.*[!@#$&*])            Ensure string has one special case letter.
         **(?=.*[0-9].*[0-9])        Ensure string has two digits.
         **(?=.*[a-z].*[a-z].*[a-z]) Ensure string has three lowercase letters.
         **.{8}                      Ensure string is of length 8.
         **$                         End anchor.
         */
        return Pattern.matches("^(?=.*[A-Z].*[A-Z])(?=.*[!@#$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z]).{8}$", password);
    }

}