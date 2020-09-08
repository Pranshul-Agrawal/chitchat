package com.example.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText loginEmail, loginPassword, loginPhone;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        TextView forgotPasswordLink = findViewById(R.id.forgot_password);
        TextView needNewAccount = findViewById(R.id.instruction_to_register_activity);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    public void signIn(View view) {

        final String email = loginEmail.getText().toString();
        final String password = loginPassword.getText().toString();

        progressDialog.setTitle("Signing in");
        progressDialog.setMessage("Please be Patient");
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
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Account Logged in Successfully", Toast.LENGTH_SHORT).show();
                                Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(mainActivityIntent);

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Incorrect password or email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    public void needNewAccount(View view) {
        Intent registerActivityIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerActivityIntent);
    }
    /* Userdefined Methods */

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