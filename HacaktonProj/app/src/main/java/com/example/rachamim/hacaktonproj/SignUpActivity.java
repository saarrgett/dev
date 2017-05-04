package com.example.rachamim.hacaktonproj;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rachamim.hacaktonproj.Model.Constants;
import com.example.rachamim.hacaktonproj.Model.User;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    protected EditText email;
    protected EditText phone;
    protected EditText licensePlate;
    protected EditText password;
    protected TextView login;
    protected Button signup;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = (EditText)findViewById(R.id.editTextEmail);
        phone = (EditText)findViewById(R.id.phoneEditText);
        licensePlate = (EditText)findViewById(R.id.licensePlate);
        password = (EditText)findViewById(R.id.editTextPassword);

        login = (TextView) findViewById(R.id.buttonLogin);
        signup = (Button)findViewById(R.id.buttonRegister);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setTitle("Signup");
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                String passwordString = password.getText().toString();
                String phoneString = phone.getText().toString();
                String licensePlateString = licensePlate.getText().toString();
                String emailString = email.getText().toString();

                passwordString = passwordString.trim();
                phoneString = phoneString.trim();
                licensePlateString = licensePlateString.trim();
                emailString = emailString.trim();

                if (passwordString.isEmpty() || emailString.isEmpty() || phoneString.isEmpty() || licensePlateString.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    progressDialog.hide();
                } else {

                    // signup
                    final String date = String.valueOf(System.currentTimeMillis());
                    final String finalEmailString = emailString;
                    final String finalPhoneString = phoneString;
                    final String finalLicensePlateString = licensePlateString;
                    final String finalPasswordString = passwordString;
                    mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                mAuth.signInWithEmailAndPassword(finalEmailString, finalPasswordString).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            User user = new User(finalEmailString, finalPhoneString, finalLicensePlateString,false,false,"",date);
                                            dbRef.child("users").child(mAuth.getCurrentUser().getUid()).setValue(user);
                                        }
                                    }
                                });
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(R.string.signup_success)
                                        .setPositiveButton(R.string.login_button_label, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(task.getException().getMessage())
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                progressDialog.hide();
                            }
                        }
                    });
                }
            }
        });
    }
}