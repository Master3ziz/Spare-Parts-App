package com.app.car_spares.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends BaseActivity {
    Button login;
    EditText edtEmail , edtPassword;
    TextView tvForgot, tvSignUp;
    FirebaseAuth mAuth;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();

        login =  findViewById(R.id.buttonlogin);
        tvForgot = findViewById(R.id.tvForgot);
        tvSignUp = findViewById(R.id.tvSignup);
        edtEmail = findViewById(R.id.edtUsername);
        edtPassword =  findViewById(R.id.editTextpassword);

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Forgot password email send code for reset password..
                String email = edtEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    edtEmail.setError("Required!");
                    edtEmail.requestFocus();
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Error : "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Required!");
                    edtEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Required!");
                    edtPassword.requestFocus();
                    return;
                }
                //Call signIn method to sign in user using firebase authentication..
                signIn();
            }
        });
    }

    private void signIn() {
        showProgressDialog("Signing In..");
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkUserType(mAuth.getCurrentUser().getUid());
                } else {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Incorrect details", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            }
        });
    }

    private void checkUserType(String userId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UsersData").child(userId);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersModel model = dataSnapshot.getValue(UsersModel.class);
                if(model.getAccount().equals("Business")){
                    hideProgressDialog();
                    Intent intent = new Intent(getApplicationContext(), SellerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else if(model.getAccount().equals("Customer")){
                    hideProgressDialog();
                    Intent intent = new Intent(getApplicationContext(), CustomerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}