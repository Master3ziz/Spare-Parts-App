package com.app.car_spares.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.app.car_spares.activities.CustomerActivity;
import com.app.car_spares.models.CartModelClass;
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

public class CartFragment extends Fragment {
    String userId;
    RecyclerView recyclerView;
    Button btnOrder;
    DatabaseReference databaseReference;
    List<CartModelClass> list;
    TextView textView, tvTotalPrice, tv;
    View view;
    Context context;
    float total = 0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        context = container.getContext();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference  = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
        list = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        recyclerView.setLayoutManager(gridLayoutManager);

        tv = view.findViewById(R.id.tv);
        textView = view.findViewById(R.id.textView);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnOrder = view.findViewById(R.id.btnOrder);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size()<1){
                    Toast.makeText(context, "No spare parts in cart!", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Orders");
                for(CartModelClass model : list){
                    String id = dbRef.push().getKey();
                    OrderModelClass modelClass = new OrderModelClass(id,model.getPartId(),userId, CustomerActivity.usersModel.getName(),
                            model.getSellerId(),model.getName(), model.getPrice(),model.getPicUrl(),model.getQuantity(),"Pending");
                    dbRef.child(id).setValue(modelClass);
                }
                Toast.makeText(context, "Order placed added successfully", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        total = 0f;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CartModelClass modelClass = snapshot.getValue(CartModelClass.class);
                    list.add(modelClass);
                    int q = Integer.parseInt(modelClass.getQuantity());
                    total += (modelClass.getPrice()*q);
                }
                if(list.size()>0){
                    textView.setText("");
                    recyclerView.setAdapter(null);
                    ItemsListAdapter adapter = new ItemsListAdapter(context,list);
                    recyclerView.setAdapter(adapter);

                    tvTotalPrice.setText(total+"");
                }else {
                    textView.setText("No items in cart!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<CartModelClass> muploadList;

        public ItemsListAdapter(Context context , List<CartModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.cart_list_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final CartModelClass cart = muploadList.get(position);
            holder.tvTitle.setText("Part: "+cart.getName());
            holder.tvPrice.setText("Price: "+cart.getPrice());
            holder.tvQty.setText("Qty: "+cart.getQuantity());
            Picasso.get().load(cart.getPicUrl()).placeholder(R.drawable.holder).into(holder.imgPic);

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation?");
                    builder.setMessage("Are you sure to remove this item from cart?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(cart.getPartId()).removeValue();
                            muploadList.remove(position);
                            notifyDataSetChanged();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
            public ImageView imgPic;
            public ImageView imgDelete;
            public TextView tvTitle;
            public TextView tvPrice;
            public TextView tvQty;

            public ImageViewHolder(View itemView) {
                super(itemView);

                imgPic = itemView.findViewById(R.id.imgPic);
                imgDelete = itemView.findViewById(R.id.imgDelete);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvQty = itemView.findViewById(R.id.tvQty);
            }
        }
    }
}
