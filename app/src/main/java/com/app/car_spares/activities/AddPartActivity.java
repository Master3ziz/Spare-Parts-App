package com.app.car_spares.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.car_spares.R;
import com.app.car_spares.models.PartModelClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddPartActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int STORAGE_PERMISSION_CODE = 100;
    public Uri mImageUri = Uri.EMPTY;
    DatabaseReference databaseReference;
    StorageReference mStorageRef ;
    StorageTask mUploadTask;
    boolean mGranted, pic = false;
    RelativeLayout layoutSelectPic;
    TextView tvSelect;
    ImageView imgCancel, imgPitch;
    EditText edtTitle, edtDescription, edtValue;
    Button btnPost, btnView;
    String title="", description="",picUrl="", key="";
    float value = 0f;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_part);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        databaseReference = FirebaseDatabase.getInstance().getReference("Parts");
        mStorageRef = FirebaseStorage.getInstance().getReference("Parts/");

        imgPitch = findViewById(R.id.imgPitch);
        layoutSelectPic = findViewById(R.id.layoutSelectPic);
        imgCancel = findViewById(R.id.imgCancel);

        tvSelect = findViewById(R.id.tvSelect);
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtValue = findViewById(R.id.edtValue);
        edtValue.setText("0");
        btnPost = findViewById(R.id.btnPost);
        btnView = findViewById(R.id.btnView);

        if(key.equals("new")){

        }else if(key.equals("edit")){
            tvSelect.setText("Edit Photo");
            title = ViewPartsActivity.model.getName();
            description = ViewPartsActivity.model.getDescription();
            value = ViewPartsActivity.model.getPrice();
            picUrl = ViewPartsActivity.model.getPicUrl();

            edtTitle.setText(title);
            edtValue.setText(value+"");
            edtDescription.setText(description);
            Picasso.get().load(picUrl).placeholder(R.drawable.holder).into(imgPitch);

            btnPost.setText("Edit Part");
//            pic = true;
        }

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutSelectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13 and greater
                    checkAndRequestPermissionsForAndroid13();
                } else {
                    // Less than Android 13
                    checkAndRequestPermissionsForOlderVersions();
                }
            }
        });
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewPartsActivity.class));
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = edtTitle.getText().toString().trim();
                String price = edtValue.getText().toString().trim();
                description = edtDescription.getText().toString().trim();

                if(!pic && key.equals("new")){
                    Toast.makeText(AddPartActivity.this, "Please select picture!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pic && key.equals("edit")){
                    Toast.makeText(AddPartActivity.this, "Please edit picture!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(title)){
                    edtTitle.setError("Required!");
                    edtTitle.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(price)){
                    edtValue.setError("Required!");
                    edtValue.requestFocus();
                    return;
                }
                value = Float.parseFloat(price);

                if(value<1){
                    edtValue.setError("Invalid Price!");
                    edtValue.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(description)){
                    edtDescription.setError("Required!");
                    edtDescription.requestFocus();
                    return;
                }
                uploadImages();
            }
        });
    }

    private void checkAndRequestPermissionsForAndroid13() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    STORAGE_PERMISSION_CODE);
        } else {
            // Permissions are already granted
            onPermissionsGranted();
        }
    }

    private void checkAndRequestPermissionsForOlderVersions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            // Permissions are already granted
            onPermissionsGranted();
        }
    }

    private void onPermissionsGranted() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                mGranted = true;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //getData() return the Uri of selected imageref
            mImageUri = data.getData();

            Log.d("picccc",data.getDataString());
            pic = true;
            Picasso.get().load(mImageUri).placeholder(R.drawable.holder).into(imgPitch);
        }else {
            Log.d("picccc","nullll");
        }
    }
    private  String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver() ;
        MimeTypeMap mime = MimeTypeMap.getSingleton() ;
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImages() {
        showProgressDialog("Uploading image..");
        final StorageReference fileref = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(mImageUri));
        mUploadTask = fileref.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() { }}, 500);

                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        try {
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            if(key.equals("new")){
                                String id = databaseReference.push().getKey();
                                PartModelClass model = new PartModelClass(id,uri.toString(),title,description,value,userId);
                                databaseReference.child(id).setValue(model);
                                edtTitle.setText("");
                                edtDescription.setText("");
                                edtValue.setText("");
                                imgPitch.setImageResource(R.drawable.holder);
                                edtTitle.requestFocus();
                                Toast.makeText(AddPartActivity.this, "Spare part added successfully", Toast.LENGTH_SHORT).show();
                            }else if(key.equals("edit")){
                                String id  = ViewPartsActivity.model.getId();
                                PartModelClass model = new PartModelClass(id,uri.toString(),title,description,value,userId);
                                databaseReference.child(ViewPartsActivity.model.getId()).setValue(model);

                                edtTitle.setText(title);
                                edtDescription.setText(description);
                                edtValue.setText(value+"");
                                edtTitle.requestFocus();
                                Toast.makeText(AddPartActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                            }
                            hideProgressDialog();

                        } catch (Exception ex ){
                            Toast.makeText(getApplicationContext()  , "err" + ex.toString() , Toast.LENGTH_LONG).show();
                            hideProgressDialog();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
            }
        });
    }
}