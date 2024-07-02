package com.app.car_spares.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.car_spares.R;
import com.app.car_spares.activities.SplashActivity;
import com.app.car_spares.activities.CustomerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {
    View view;
    Context context;
    ImageView editName;
    EditText edtName, edtUsername, edtPassword;
    Button btnResetPassword, btnSignOut;
    int count = 0;
    DatabaseReference databaseReference;
    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = container.getContext();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("UsersData").child(userId);

        edtName = view.findViewById(R.id.edtName);
        edtUsername = view.findViewById(R.id.edtUsername);
        edtPassword = view.findViewById(R.id.edtPassword);
        btnResetPassword = view.findViewById(R.id.btnDelete);
        btnSignOut = view.findViewById(R.id.btnSignOut);
        editName = view.findViewById(R.id.imgeditName);

        edtName.setText(CustomerActivity.usersModel.getName());
        edtPassword.setText(CustomerActivity.usersModel.getPassword());
        edtUsername.setText(CustomerActivity.usersModel.getPassword());

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
                    Toast.makeText(context, "Name is updated", Toast.LENGTH_SHORT).show();
                    edtName.setText(name);
                    edtName.setEnabled(false);
                }
            }
        });
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(CustomerActivity.usersModel.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "Error : "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(context, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}
