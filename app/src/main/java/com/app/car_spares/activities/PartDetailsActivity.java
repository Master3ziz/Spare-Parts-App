package com.app.car_spares.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.car_spares.R;
import com.app.car_spares.models.CartModelClass;
import com.app.car_spares.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PartDetailsActivity extends AppCompatActivity {

    ImageView imgPic;
    TextView tvItemName, tvPrice, tvDescription, tvSeller;
    Button btnBook;
    String userId;
    DatabaseReference databaseReference;
    AlertDialog alertDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_details);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference  =FirebaseDatabase.getInstance().getReference("Cart").child(userId);

        imgPic = findViewById(R.id.imgPic);
        tvItemName = findViewById(R.id.tvItemName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvSeller = findViewById(R.id.tvSeller);
        btnBook = findViewById(R.id.btnBook);

        tvItemName.setText("Name: "+ CustomerActivity.model.getName());
        tvPrice.setText("Price: "+ CustomerActivity.model.getPrice());
        tvDescription.setText(CustomerActivity.model.getDescription());
        getSellerName();

        Picasso.get().load(CustomerActivity.model.getPicUrl()).placeholder(R.drawable.holder).into(imgPic);

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dailogBuilder = new AlertDialog.Builder(PartDetailsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
                dailogBuilder.setView(dialogView);

                final EditText edtQuantity = dialogView.findViewById(R.id.edtQuantity);
                final Button btnSub = dialogView.findViewById(R.id.btnSub);

                btnSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String quantity = edtQuantity.getText().toString().trim();
                        if(quantity.isEmpty()){
                            edtQuantity.setError("Required!");
                            edtQuantity.requestFocus();
                            return;
                        }
                        CartModelClass cart = new CartModelClass(CustomerActivity.model.getId(), CustomerActivity.model.getPicUrl(),
                                CustomerActivity.model.getName(), CustomerActivity.model.getPrice(), CustomerActivity.model.getUserId(),quantity);
                        databaseReference.child(CustomerActivity.model.getId()).setValue(cart);
                        Toast.makeText(getApplicationContext(), "Item added to cart", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
                alertDialog = dailogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

    }

    private void getSellerName() {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UsersData").child(CustomerActivity.model.getUserId());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersModel usersModel = dataSnapshot.getValue(UsersModel.class);
                tvSeller.setText("Seller: "+usersModel.getName());

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }
}