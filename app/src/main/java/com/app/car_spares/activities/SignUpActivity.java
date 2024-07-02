package com.app.car_spares.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.car_spares.R;
import com.app.car_spares.models.UsersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends BaseActivity {
    EditText edtName, edtPassword , edtUsername;
    TextView tvLogin;
    Context context;
    Button btnReg;
    private FirebaseAuth mAuth;
    public String name, email, password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        context = SignUpActivity.this;
        edtName = findViewById(R.id.edtname);
        edtPassword = findViewById(R.id.edtpass);
        edtUsername = findViewById(R.id.edtUsername);
        tvLogin = findViewById(R.id.tvLogin);
        btnReg = findViewById(R.id.btnreg);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edtName.getText().toString();
                password = edtPassword.getText().toString().trim();
                email = edtUsername.getText().toString().trim();

                if(name.isEmpty()){
                    edtName.setError("Required!");
                    edtName.requestFocus();
                    return;
                }
                if(email.isEmpty()){
                    edtUsername.setError("Required!");
                    edtUsername.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    edtPassword.setError("Required!");
                    edtPassword.requestFocus();
                    return;
                }
                if(password.length()<6){
                    edtPassword.setError("Invalid password!");
                    edtPassword.requestFocus();
                    return;
                }
                createAccount(email,password);
            }
        });
    }

    private void createAccount(final String email, final String password) {
        showProgressDialog("Please wait..");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveUserData();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                }
            }
        });
    }

    private void saveUserData() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UsersData");
                    UsersModel modeClass = new UsersModel(mAuth.getCurrentUser().getUid(),name,email,password,SelectionActivity.choice);
                    dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(modeClass);
                    Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    },2000);
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}