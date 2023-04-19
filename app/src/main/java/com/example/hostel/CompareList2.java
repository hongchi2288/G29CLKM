package com.example.hostel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class CompareList2 extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_list2);

        ListView listView = findViewById(R.id.myListView);
        ImageView BackButton = findViewById(R.id.BackButton);



        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Get a reference to the "Danish1" node in the Realtime Database
        //mDatabase = FirebaseDatabase.getInstance().getReference("DanishHouse/Danish1");
        mDatabase = FirebaseDatabase.getInstance().getReference("Hostel");


        // Add a ValueEventListener to listen for changes in the data
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Map<String, Object>> dataList = new ArrayList<>();

                // Loop through all the children of the DanishHouse node
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Loop through all the children of each DanishHouse child node
                    for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                        Map<String, Object> data = (Map<String, Object>) grandChildSnapshot.getValue();
                        dataList.add(data);
                    }
                }

                // Create a custom adapter to display the data in a ListView
                CompareList2.CustomAdapter adapter = new CompareList2.CustomAdapter(dataList);

                listView.setAdapter(adapter);

                //===============================================================================================
                // Set click listener on the ListView items
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Get the clicked item's data
                        Map<String, Object> data = dataList.get(position);
                        // Start the detail activity and pass the data as extras
                        Intent intent = new Intent(CompareList2.this, CompareActivity.class);
                        intent.putExtra("Agent2", data.get("Agent").toString());
                        intent.putExtra("Address2", data.get("Address").toString());
                        intent.putExtra("HouseNo2", data.get("HouseNo").toString());
                        intent.putExtra("Type2", data.get("Type").toString());
                        intent.putExtra("Price2", data.get("Price").toString());
                        intent.putExtra("imageUrl2", data.get("imageUrl").toString());
                        intent.putExtra("image22", data.get("image2").toString());
                        intent.putExtra("image32", data.get("image3").toString());
                        startActivity(intent);
                        // Start the detail activity and pass the "key" as an extra


//                        Map<String, Object> data = dataList.get(position);
//                        // Get the parent node's key (e.g. "Danish1", "Danish2", etc.)
//                        String key = childSnapshot.getKey();
//
//                        // Start the detail activity and pass the key as an extra
//                        Intent intent = new Intent(HostelList.this, DetailActivity.class);
//                        intent.putExtra("key", key);
//                        startActivity(intent);


                    }
                });
                //===============================================================================================
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("HostelList", "onCancelled", databaseError.toException());
            }
        });

    }

    class CustomAdapter extends ArrayAdapter<Map<String, Object>> {
        private FirebaseStorage storage;
        private StorageReference storageRef;
        private StorageReference imageRef;
        public CustomAdapter(List<Map<String, Object>> dataList) {
            super(CompareList2.this, R.layout.list_item_layout, dataList);

            // Initialize the memory cache with 1/8 of the available memory
            int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // Return the size of the bitmap in bytes
                    return bitmap.getByteCount();
                }
            };



            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            //imageRef = storageRef.child("ImageHouse/house1.jpg"); // or "ImageHouse/house2.jpg"

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.list_item_layout, parent, false);

            TextView TextAgent = row.findViewById(R.id.TextAgent);
            TextView houseNoTextView = row.findViewById(R.id.houseNoTextView);
            TextView typeTextView = row.findViewById(R.id.typeTextView);
            TextView priceTextView = row.findViewById(R.id.priceTextView);
            ImageView imageView = row.findViewById(R.id.imageView);



            Map<String, Object> data = getItem(position);
            String agent = data.get("Agent").toString();
            String price = data.get("Price").toString();
            String number = data.get("HouseNo").toString();
            String type = data.get("Type").toString();
            String imageUrl = data.get("imageUrl").toString();

            TextAgent.setText(agent);
            houseNoTextView.setText("House No : "+ number);
            typeTextView.setText(type);
            priceTextView.setText("RM " + price);



            // Check if the image is in the cache
            Bitmap bitmap = mMemoryCache.get(imageUrl);
            if (bitmap != null) {
                // Load the image from the cache
                imageView.setImageBitmap(bitmap);
            } else {
                // Get a reference to the image in Firebase Storage and download it into a Bitmap
                imageRef = storageRef.child(imageUrl);
                final long ONE_MEGABYTE = 1024 * 1024;
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Convert the byte array into a Bitmap and set it to the ImageView
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);

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




            return row;
        }
    }
}