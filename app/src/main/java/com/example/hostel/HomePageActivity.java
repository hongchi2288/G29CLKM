package com.example.hostel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomePageActivity extends AppCompatActivity {

    private ImageView Image5, Image6;
    private ImageView Image1, Image2, Image3;
    private TextView Text5, Text6, TextAgent;
    private FirebaseFirestore db;


    private DatabaseReference mDatabase;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;

    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Image5 = findViewById(R.id.Image5);
        Image6 = findViewById(R.id.Image5);
        Text5 = findViewById(R.id.Text5);
        Text6 = findViewById(R.id.Text6);
        TextAgent = findViewById(R.id.TextAgent);


        Image1 = findViewById(R.id.Image1);
        Image2 = findViewById(R.id.Image2);
        Image3 = findViewById(R.id.Image3);

        TextView moreButton = findViewById(R.id.moreButton);
        ImageView buttonCompare = findViewById(R.id.buttonCompare);

        LinearLayout ktBox = findViewById(R.id.ktBox);
        LinearLayout danishBox = findViewById(R.id.danishBox);
        LinearLayout villasBox = findViewById(R.id.villasBox);

        LinearLayout linear23 = findViewById(R.id.linear23);
        ImageView ButtonProfile=findViewById(R.id. buttonProfile);

        ButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this,UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        linear23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this,FilterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ktBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this,KTManagement.class);
                startActivity(intent);
                finish();
            }
        });

        danishBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this,DanishHouse.class);
                startActivity(intent);
                finish();
            }
        });

        villasBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this,VillasCondo.class);
                startActivity(intent);
                finish();
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this,HostelList.class);
                startActivity(intent);
                finish();
            }
        });
        buttonCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this,SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // Load the original image
        Bitmap originalBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.ktlogo);
        Bitmap originalBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.danishlogo);
        Bitmap originalBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.villaslogo);
        // Resize the image
        int width1 = originalBitmap1.getWidth() / 2;
        int height1 = originalBitmap1.getHeight() / 2;
        int width2 = originalBitmap2.getWidth() / 2;
        int height2 = originalBitmap2.getHeight() / 2;
        int width3 = originalBitmap3.getWidth() / 2;
        int height3 = originalBitmap3.getHeight() / 2;

        Bitmap resizedBitmap1 = Bitmap.createScaledBitmap(originalBitmap1, width1, height1, false);
        Bitmap resizedBitmap2 = Bitmap.createScaledBitmap(originalBitmap1, width2, height2, false);
        Bitmap resizedBitmap3 = Bitmap.createScaledBitmap(originalBitmap1, width3, height3, false);
        // Display the resized image
        Image1.setImageBitmap(resizedBitmap1);
        Image2.setImageBitmap(resizedBitmap1);
        Image3.setImageBitmap(resizedBitmap1);

        // Load and resize the image using Picasso
        Picasso.get().load(R.drawable.ktlogo).resize(200, 200).into(Image1);
        Picasso.get().load(R.drawable.danishlogo).resize(200, 200).into(Image2);
        Picasso.get().load(R.drawable.villaslogo).resize(200, 200).into(Image3);


        //mDatabase = FirebaseDatabase.getInstance().getReference("DanishHouse");

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


        mDatabase = FirebaseDatabase.getInstance().getReference("Hostel");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> childNames = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    childNames.add(childSnapshot.getKey());
                }

                String randomChildName = childNames.get(new Random().nextInt(childNames.size()));
                DataSnapshot randomChildSnapshot = dataSnapshot.child(randomChildName).getChildren().iterator().next();

                String type = randomChildSnapshot.child("Type").getValue(String.class);
                String price = randomChildSnapshot.child("Price").getValue(String.class);
                String imageUrl = randomChildSnapshot.child("imageUrl").getValue(String.class);
                String agent = randomChildSnapshot.child("Agent").getValue(String.class);

                TextAgent.setText(agent);
                Text5.setText(type);
                Text6.setText("RM " + price);

                // Check if the image is in the cache
                Bitmap bitmap = mMemoryCache.get(imageUrl);
                if (bitmap != null) {
                    // Load the image from the cache
                    Image5.setImageBitmap(bitmap);
                } else {
                    // Get a reference to the image in Firebase Storage and download it into a Bitmap
                    imageRef = storageRef.child(imageUrl);
                    final long ONE_MEGABYTE = 1024 * 1024;
                    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Convert the byte array into a Bitmap and set it to the ImageView
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Image5.setImageBitmap(bitmap);

                            // Add the bitmap to the cache
                            mMemoryCache.put(imageUrl, bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
}