package com.app.car_spares.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.car_spares.R;
import com.app.car_spares.models.OrderModelClass;
import com.app.car_spares.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrackOrderFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    View view;
    Context context;
    RecyclerView recyclerView;
    TextView textView, tv;
    List<OrderModelClass> list;
    EventsListAdapter adapter;
    public static OrderModelClass model;
    DatabaseReference databaseReference;
    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_track_order, container, false);
        context = container.getContext();

        showProgressDialog("Loading data..");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
        list = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        textView = view.findViewById(R.id.textView);
        tv = view.findViewById(R.id.tv);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderModelClass modelClass = snapshot.getValue(OrderModelClass.class);
                    if(userId.equals(modelClass.getUserId())){
                        list.add(modelClass);
                    }
                }
                if(list.size()>0){
                    textView.setText("");
                    recyclerView.setAdapter(null);
                    adapter = new EventsListAdapter(context,list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No orders!");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<OrderModelClass> muploadList;

        public EventsListAdapter(Context context , List<OrderModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public EventsListAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.order_list_layout, parent , false);
            return (new EventsListAdapter.ImageViewHolder(v));
        }

        private void getSellerName(String sellerId, TextView tvSeller) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UsersData").child(sellerId);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UsersModel usersModel = dataSnapshot.getValue(UsersModel.class);
                    tvSeller.setText("Seller: "+usersModel.getName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}});
        }
        @Override
        public void onBindViewHolder(final EventsListAdapter.ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final OrderModelClass order = muploadList.get(position);
            holder.tvPitch.setText("Part: "+order.getPartName());
            holder.tvPrice.setText("Price: "+order.getPartPrice());
            holder.tvLocation.setText("Quantity: "+order.getQuantity());
            holder.tvStatus.setText("Order Status: "+order.getStatus());
            Picasso.get().load(order.getPartPic()).placeholder(R.drawable.holder).into(holder.imgPic);

            getSellerName(order.getSellerId(),holder.tvUserName);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!order.getStatus().equals("Canceled")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmation?");
                        builder.setMessage("Do you want to cancel this order?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.child(order.getId()).child("status").setValue("Canceled");
                                holder.tvStatus.setText("Order Status: Canceled");
                                Toast.makeText(mcontext, "Order canceled", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }else {
                        Toast.makeText(mcontext, "Order already canceled!", Toast.LENGTH_SHORT).show();
                    }
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

    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
