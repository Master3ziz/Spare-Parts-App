package com.app.car_spares.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.app.car_spares.R;
import com.app.car_spares.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectionActivity extends BaseActivity {

    Button btnBusiness, btnCustomer;
    public static String choice="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        btnBusiness = findViewById(R.id.btnBusiness);
        btnCustomer = findViewById(R.id.btnCustomer);

        btnBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Business";
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });
        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Customer";
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });
    }

    //This method checks user login status..
    @Override
    protected void onStart() {
        super.onStart();

        if(isOnline()){
            showProgressDialog("Checking login...");
            checkUserType(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
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

    boolean isOnline(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            return true;
        }
        return false;
    }
}