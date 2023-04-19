package com.example.hostel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;

    private LruCache<String, Bitmap> mMemoryCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView buttonBack = findViewById(R.id.buttonBack);
        TextView addressTextView = findViewById(R.id.addressTextView);
        TextView houseNoTextView = findViewById(R.id.houseNo);
        TextView typeTextView = findViewById(R.id.typeTextView);
        TextView priceTextView = findViewById(R.id.priceTextView);
        TextView TextAgent = findViewById(R.id.TextAgent);
        ImageView Image1 = findViewById(R.id.Image1);
        ImageView Image2 = findViewById(R.id.Image2);
        ImageView Image3 = findViewById(R.id.Image3);

        Button BookButton = findViewById(R.id.BookButton);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize the memory cache with 1/8 of the available memory
        int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // Return the size of the bitmap in bytes
                return bitmap.getByteCount();
            }
        };

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Retrieve the data from the extras
        Intent intent = getIntent();
        String agent = intent.getStringExtra("Agent");
        String address = intent.getStringExtra("Address");
        String houseNo = intent.getStringExtra("HouseNo");
        String type = intent.getStringExtra("Type");
        String price = intent.getStringExtra("Price");
        String imageUrl = intent.getStringExtra("imageUrl");
        String image2 = intent.getStringExtra("image2");
        String image3 = intent.getStringExtra("image3");



        BookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, BookingPage.class);
                intent.putExtra("Agent",agent);
                intent.putExtra("HouseNo", houseNo);
                startActivity(intent);
            }
        });


        TextAgent.setText(agent);
        addressTextView.setText(address);
        houseNoTextView.setText("House No : " + houseNo);
        typeTextView.setText(type);
        priceTextView.setText("RM " + price);

        // Load the image from the imageUrl
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef1 = storageRef.child(imageUrl);
        StorageReference imageRef2 = storageRef.child(image2);
        StorageReference imageRef3 = storageRef.child(image3);

        // Check if the image is in the cache
        Bitmap bitmap = mMemoryCache.get(imageUrl);
        if (bitmap != null) {
            // Load the image from the cache
            Image1.setImageBitmap(bitmap);
            Image2.setImageBitmap(bitmap);
            Image3.setImageBitmap(bitmap);
        } else {
            // Get a reference to the image in Firebase Storage and download it into a Bitmap
            imageRef1 = storageRef.child(imageUrl);
            imageRef2 = storageRef.child(image2);
            imageRef3 = storageRef.child(image3);

            final long ONE_MEGABYTE = 1024 * 1024;
            imageRef1.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Convert the byte array into a Bitmap and set it to the ImageView
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Image1.setImageBitmap(bitmap);

                    // Add the bitmap to the cache
                    mMemoryCache.put(imageUrl, bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });


            imageRef2.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Convert the byte array into a Bitmap and set it to the ImageView
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Image2.setImageBitmap(bitmap);

                    // Add the bitmap to the cache
                    mMemoryCache.put(image2,bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            imageRef3.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Convert the byte array into a Bitmap and set it to the ImageView
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Image3.setImageBitmap(bitmap);

                    // Add the bitmap to the cache
                    mMemoryCache.put(image3,bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }










    }
}