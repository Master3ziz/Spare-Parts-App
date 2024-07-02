package com.app.car_spares.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.car_spares.R;
import com.app.car_spares.activities.PartDetailsActivity;
import com.app.car_spares.activities.CustomerActivity;
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

public class HomeFragment extends Fragment {
    View view;
    Context context;
    List<PartModelClass> list;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    EditText edtSearch;
    TextView textView;
    EventsListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = container.getContext();

        showProgressDialog("Loading data..");
        databaseReference = FirebaseDatabase.getInstance().getReference("Parts");
        list = new ArrayList<>();

        textView = view.findViewById(R.id.textView);
        edtSearch = view.findViewById(R.id.edtSearch);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2);
        recyclerView = view.findViewById(R.id.recyclerView) ;
        recyclerView.setHasFixedSize(true); ;
        recyclerView.setLayoutManager(gridLayoutManager);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }});

        return view;
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

    @Override
    public void onStart() {
        super.onStart();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PartModelClass modelClass = snapshot.getValue(PartModelClass.class);
                    list.add(modelClass);
                }
                if(list.size()>0){
                    textView.setText("");
                    recyclerView.setAdapter(null);
                    EventsListAdapter adapter = new EventsListAdapter(context,list);
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

    public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<PartModelClass> muploadList;

        public EventsListAdapter(Context context , List<PartModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public EventsListAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.parts_list_layout, parent , false);
            return (new EventsListAdapter.ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final EventsListAdapter.ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final PartModelClass part = muploadList.get(position);
            holder.tvName.setText(part.getName());
            holder.tvPrice.setText("Price: "+part.getPrice());

            Picasso.get().load(part.getPicUrl()).placeholder(R.drawable.holder).into(holder.imgPic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomerActivity.model = part;
                    Intent intent = new Intent(context, PartDetailsActivity.class);
                    startActivity(intent);
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

            public ImageViewHolder(View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tvName);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                imgPic = itemView.findViewById(R.id.imgPic);
            }
        }

        public void filterList(List<PartModelClass> searchList) {
            this.muploadList = searchList;
            notifyDataSetChanged();
        }
    }

    private ProgressDialog mProgressDialog;
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
