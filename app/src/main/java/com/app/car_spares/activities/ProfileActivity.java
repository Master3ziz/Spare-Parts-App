package com.app.car_spares.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.car_spares.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class ProfileActivity extends AppCompatActivity {
    ImageView editName;
    EditText edtName, edtUsername, edtPassword;
    Button btnResetPassword;
    int count = 0;
    DatabaseReference databaseReference;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        edtName = findViewById(R.id.edtName);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnResetPassword = findViewById(R.id.btnDelete);
        editName = findViewById(R.id.imgeditName);

        edtName.setText(SellerActivity.usersModel.getName());
        edtPassword.setText(SellerActivity.usersModel.getPassword());
        edtUsername.setText(SellerActivity.usersModel.getPassword());

        edtName.setEnabled(false);
        edtPassword.setEnabled(false);
        edtUsername.setEnabled(false);

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                if(count%2 != 0) {
                    edtName.setEnabled(true);
                    edtName.requestFocus();
                }else {
                    String name = edtName.getText().toString().trim();
                    databaseReference.child("name").setValue(name);
                    Toast.makeText(getApplicationContext(), "Name is updated", Toast.LENGTH_SHORT).show();
                    edtName.setText(name);
                    edtName.setEnabled(false);
                }
            }
        });
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(SellerActivity.usersModel.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
    }
}