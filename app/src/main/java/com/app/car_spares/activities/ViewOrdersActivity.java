package com.app.car_spares.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.car_spares.R;
import com.app.car_spares.models.OrderModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewOrdersActivity extends BaseActivity {

    RecyclerView recyclerView;
    TextView textView;
    List<OrderModelClass> list;
    EventsListAdapter adapter;
    public static OrderModelClass model;
    EditText edtSearch;
    DatabaseReference databaseReference;
    String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        showProgressDialog("Loading data..");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        list = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        textView = findViewById(R.id.textView);
        edtSearch = findViewById(R.id.edtSearch);

        //Search bar code..
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }});

    }

    @Override
    protected void onStart() {
        super.onStart();

        String str = "Pending";
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderModelClass modelClass = snapshot.getValue(OrderModelClass.class);
                    if(userId.equals(modelClass.getSellerId()) && str.equals(modelClass.getStatus())){
                        list.add(modelClass);
                    }
                }
                if(list.size()>0){
                    textView.setText("");
                    recyclerView.setAdapter(null);
                    adapter = new EventsListAdapter(ViewOrdersActivity.this,list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No new orders!");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    //This method apply searching in list..
    private void filter(String text) {
        //new array list that will hold the filtered data
        List<OrderModelClass> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (OrderModelClass s : list) {
            //if the existing elements contains the search input
            if (s.getPartName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        try {
            adapter.filterList(filterdNames);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<OrderModelClass> muploadList;

        public EventsListAdapter(Context context , List<OrderModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.order_list_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final OrderModelClass order = muploadList.get(position);
            holder.tvUserName.setText("Customer: "+order.getUserName());
            holder.tvPitch.setText("Part: "+order.getPartName());
            holder.tvPrice.setText("Price: "+order.getPartPrice());
            holder.tvLocation.setText("Quantity: "+order.getQuantity());
            holder.tvStatus.setText("Order Status: "+order.getStatus());
            Picasso.get().load(order.getPartPic()).placeholder(R.drawable.holder).into(holder.imgPic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrdersActivity.this);
                    builder.setTitle("Confirmation?");
                    builder.setMessage("Do you want to accept or reject this order?").setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(order.getId()).child("status").setValue("Accept");
                            muploadList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(mcontext, "Order accepted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(order.getPartId()).child("status").setValue("Reject");
                            muploadList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(mcontext, "Order rejected", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder{
            public TextView tvUserName;
            public TextView tvPitch;
            public TextView tvLocation;
            public TextView tvPrice;
            public TextView tvStatus;
            public ImageView imgPic;

            public ImageViewHolder(View itemView) {
                super(itemView);

                tvUserName = itemView.findViewById(R.id.tvUserName);
                tvPitch = itemView.findViewById(R.id.tvPitch);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                imgPic = itemView.findViewById(R.id.imgPic);
                tvStatus = itemView.findViewById(R.id.tvStatus);
            }
        }

        public void filterList(List<OrderModelClass> searchList) {
            this.muploadList = searchList;
            notifyDataSetChanged();
        }
    }
}