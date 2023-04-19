package com.example.hostel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class CompareActivity extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;

    private LruCache<String, Bitmap> mMemoryCache;

    private static final int REQUEST_CODE_SELECT1 = 1;
    private static final int REQUEST_CODE_SELECT2 = 2;


    // Member variables to store extracted data from CompareList1 and CompareList2 activities
    private String agent1, address1, houseNo1, type1, price1, imageUrl1, image21, image31;
    private String agent2, address2, houseNo2, type2, price2, imageUrl2, image22, image32;

    private boolean isSelect1DataAvailable = false;
    private boolean isSelect2DataAvailable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);


        ImageView select1 = findViewById(R.id.select1);
        ImageView select2 = findViewById(R.id.select2);
        ImageView buttonBack = findViewById(R.id.buttonBack);

//        ImageView imageview1 = findViewById(R.id.imageview1);
//        ImageView imageView2 = findViewById(R.id.imageview2);
//
//        TextView houseText1 = findViewById(R.id.houseText1);
//        TextView houseText2 = findViewById(R.id.houseText2);
//
//        TextView priceText1 = findViewById(R.id.priceText1);
//        TextView priceText2 = findViewById(R.id.priceText2);
//
//        TextView agentText1 = findViewById(R.id.agentText1);
//        TextView agentText2 = findViewById(R.id.agentText2);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        select1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompareActivity.this, CompareList1.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT1);
            }
        });

        select2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompareActivity.this, CompareList2.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT2);
            }
        });

        // Initialize the memory cache with 1/8 of the available memory
        int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // Return the size of the bitmap in bytes
                return bitmap.getByteCount();
            }
        };


    }

    // Method to update UI with data from both CompareList1 and CompareList2
    private void updateUI() {
        if (isSelect1DataAvailable && isSelect2DataAvailable) {
            // Both sets of data are available, update the UI with both
            ImageView imageview1 = findViewById(R.id.imageview1);
            ImageView imageview2 = findViewById(R.id.imageview2);

            TextView houseText1 = findViewById(R.id.houseText1);
            TextView houseText2 = findViewById(R.id.houseText2);

            TextView priceText1 = findViewById(R.id.priceText1);
            TextView priceText2 = findViewById(R.id.priceText2);

            TextView agentText1 = findViewById(R.id.agentText1);
            TextView agentText2 = findViewById(R.id.agentText2);

            // Set data from CompareList1
            houseText1.setText(address1 + ", " + houseNo1);
            priceText1.setText(price1);
            agentText1.setText(agent1);

            // Set data from CompareList2
            houseText2.setText(address2 + ", " + houseNo2);
            priceText2.setText(price2);
            agentText2.setText(agent2);

//            // Load images using Picasso or Glide
//            Picasso.get().load(imageUrl1).into(imageview1);
//            Picasso.get().load(imageUrl2).into(imageview2);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT1) {
                // Get data from CompareList1
                agent1 = data.getStringExtra("Agent1");
                address1 = data.getStringExtra("Address1");
                houseNo1 = data.getStringExtra("HouseNo1");
                type1 = data.getStringExtra("Type1");
                price1 = data.getStringExtra("Price1");
                imageUrl1 = data.getStringExtra("imageUrl1");
                image21 = data.getStringExtra("image21");
                image31 = data.getStringExtra("image31");

                isSelect1DataAvailable = true;

                // Update UI with both sets of data
                updateUI();
            } else if (requestCode == REQUEST_CODE_SELECT2) {
                // Get data from CompareList2
                agent2 = data.getStringExtra("Agent2");
                address2 = data.getStringExtra("Address2");
                houseNo2 = data.getStringExtra("HouseNo2");
                type2 = data.getStringExtra("Type2");
                price2 = data.getStringExtra("Price2");
                imageUrl2 = data.getStringExtra("imageUrl2");
                image22 = data.getStringExtra("image22");
                image32 = data.getStringExtra("image32");

                isSelect2DataAvailable = true;

                // Update UI with both sets of data
                updateUI();

            }
        }
    }
}


