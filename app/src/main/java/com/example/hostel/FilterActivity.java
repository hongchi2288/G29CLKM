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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private LruCache<String, Bitmap> mMemoryCache;
    // Declare dataList as a member variable
    private List<Map<String, Object>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        EditText minPrice = findViewById(R.id.minPrice);
        EditText maxPrice = findViewById(R.id.maxPrice);
        Button submitButton = findViewById(R.id.submitButton);
        ImageView buttonBack = findViewById(R.id.buttonBack);

        ListView listView = findViewById(R.id.myListView);


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(FilterActivity.this,HomePageActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        // Get a reference to the "Danish1" node in the Realtime Database
        //mDatabase = FirebaseDatabase.getInstance().getReference("DanishHouse/Danish1");
        mDatabase = FirebaseDatabase.getInstance().getReference("Hostel");



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String minPriceValue = minPrice.getText().toString();
                String maxPriceValue = maxPrice.getText().toString();

                // Check if user has entered valid values
                if (minPriceValue.isEmpty() || maxPriceValue.isEmpty()) {
                    Toast.makeText(FilterActivity.this, "Please enter both minimum and maximum price.", Toast.LENGTH_SHORT).show();
                } else {
                    // Convert input values to integers
                    int minPrice = Integer.parseInt(minPriceValue);
                    int maxPrice = Integer.parseInt(maxPriceValue);

                    // Filter the data based on minPrice and maxPrice
                    List<Map<String, Object>> filteredData = new ArrayList<>();
                    for (Map<String, Object> data : dataList) {
                        int price = Integer.parseInt(data.get("Price").toString());
                        if (price >= minPrice && price <= maxPrice) {
                            filteredData.add(data);
                        }
                    }

                    // Create a custom adapter with filtered data to display in the ListView
                    FilterActivity.CustomAdapter adapter = new FilterActivity.CustomAdapter(filteredData);
                    listView.setAdapter(adapter);

                    // Set click listener on the ListView items
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Get the clicked item's data
                            Map<String, Object> data = filteredData.get(position);
                            // Start the detail activity and pass the data as extras
                            Intent intent = new Intent(FilterActivity.this, DetailActivity.class);
                            intent.putExtra("Agent", data.get("Agent").toString());
                            intent.putExtra("Address", data.get("Address").toString());
                            intent.putExtra("HouseNo", data.get("HouseNo").toString());
                            intent.putExtra("Type", data.get("Type").toString());
                            intent.putExtra("Price", data.get("Price").toString());
                            intent.putExtra("imageUrl", data.get("imageUrl").toString());
                            intent.putExtra("image2", data.get("image2").toString());
                            intent.putExtra("image3", data.get("image3").toString());
                            startActivity(intent);
                            // Start the detail activity and pass the "key" as an extra

                        }
                    });
                }
            }
        });

        // Add a ValueEventListener to listen for changes in the data
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Remove the re-initialization of dataList
                dataList.clear();

                // Loop through all the children of the DanishHouse node
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Loop through all the children of each DanishHouse child node
                    for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                        Map<String, Object> data = (Map<String, Object>) grandChildSnapshot.getValue();
                        dataList.add(data);
                    }
                }

                // Create a custom adapter to display the data in a ListView
                CustomAdapter adapter = new CustomAdapter(dataList);

                listView.setAdapter(adapter);

                //===============================================================================================
                // Set click listener on the ListView items
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Get the clicked item's data
                        Map<String, Object> data = dataList.get(position);
                        // Start the detail activity and pass the data as extras
                        Intent intent = new Intent(FilterActivity.this, DetailActivity.class);
                        intent.putExtra("Agent", data.get("Agent").toString());
                        intent.putExtra("Address", data.get("Address").toString());
                        intent.putExtra("HouseNo", data.get("HouseNo").toString());
                        intent.putExtra("Type", data.get("Type").toString());
                        intent.putExtra("Price", data.get("Price").toString());
                        intent.putExtra("imageUrl", data.get("imageUrl").toString());
                        intent.putExtra("image2", data.get("image2").toString());
                        intent.putExtra("image3", data.get("image3").toString());
                        startActivity(intent);
                        // Start the detail activity and pass the "key" as an extra

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
        public CustomAdapter(List<Map<String, Object>> data) {
            super(FilterActivity.this, R.layout.list_item_layout, data);

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




