package com.app.car_spares.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.car_spares.R;
import com.app.car_spares.fragments.CartFragment;
import com.app.car_spares.fragments.HomeFragment;
import com.app.car_spares.fragments.ProfileFragment;
import com.app.car_spares.fragments.TrackOrderFragment;
import com.app.car_spares.models.CartModelClass;
import com.app.car_spares.models.PartModelClass;
import com.app.car_spares.models.UsersModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerActivity extends BaseActivity {

    public static PartModelClass model;
    public static CartModelClass model2;
    String userId;
    public static UsersModel usersModel;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getFragmentManager().popBackStack();
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_cart:
                    getFragmentManager().popBackStack();
                    selectedFragment = new CartFragment();
                    break;
                case R.id.navigation_track_order:
                    getFragmentManager().popBackStack();
                    selectedFragment = new TrackOrderFragment();
                    break;
                case R.id.navigation_profile:
                    getFragmentManager().popBackStack();
                    selectedFragment = new ProfileFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            return true;
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onStart() {
        super.onStart();

        showProgressDialog("Loading data.");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UsersData").child(userId);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersModel = dataSnapshot.getValue(UsersModel.class);
                Fragment selectedFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }});
    }
}