package com.app.car_spares.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.car_spares.R;
import com.app.car_spares.models.PartModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewPartsActivity extends BaseActivity {

    RecyclerView recyclerView;
    TextView textView;
    List<PartModelClass> list;
    EventsListAdapter adapter;
    public static PartModelClass model;
    EditText edtSearch;
    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_parts);

        showProgressDialog("Loading data..");
        databaseReference = FirebaseDatabase.getInstance().getReference("Parts");
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

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PartModelClass modelClass = snapshot.getValue(PartModelClass.class);
                    if(userId.equals(modelClass.getUserId())){
                        list.add(modelClass);
                    }
                }
                if(list.size()>0){
                    textView.setText("");
                    recyclerView.setAdapter(null);
                    EventsListAdapter adapter = new EventsListAdapter(ViewPartsActivity.this,list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No spare parts added!");
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
        List<PartModelClass> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (PartModelClass s : list) {
            //if the existing elements contains the search input
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
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
        private List<PartModelClass> muploadList;

        public EventsListAdapter(Context context , List<PartModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.parts_list_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final PartModelClass part = muploadList.get(position);
            holder.tvName.setText(part.getName());
            holder.tvPrice.setText("Price: "+part.getPrice());

            Picasso.get().load(part.getPicUrl()).placeholder(R.drawable.holder).into(holder.imgPic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model = part;
                    Intent intent = new Intent(getApplicationContext(), AddPartActivity.class);
                    intent.putExtra("key","edit");
                    startActivity(intent);
                }
            });
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewPartsActivity.this);
                    builder.setTitle("Confirmation?");
                    builder.setMessage("Are you sure to this this part?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(part.getId()).removeValue();
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
            public TextView tvName;
            public TextView tvPrice;
            public ImageView imgPic;
            public ImageView imgDelete;

            public ImageViewHolder(View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tvName);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                imgPic = itemView.findViewById(R.id.imgPic);
                imgDelete = itemView.findViewById(R.id.imgDelete);
            }
        }

        public void filterList(List<PartModelClass> searchList) {
            this.muploadList = searchList;
            notifyDataSetChanged();
        }
    }
}