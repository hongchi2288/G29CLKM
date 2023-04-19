package com.example.hostel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingPage extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView chooseDate;
    private Button buttonUpload;
    private ImageView buttonBack;
    private String DateSelected;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);

        calendarView = findViewById(R.id.calendarView);
        TextView chooseHouse = findViewById(R.id.chooseHouse);
        chooseDate = findViewById(R.id.chooseDate);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonBack = findViewById(R.id.buttonBack);


        Intent intent = getIntent();
        String agent = intent.getStringExtra("Agent");
        String houseNo = intent.getStringExtra("HouseNo");
        chooseHouse.setText(agent+" "+houseNo);
        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                DateSelected = dayOfMonth + "/" + (month+1) + "/" + year;

                calendarClicked();

            }
        });

//        buttonUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //previous
//                //databaseReference.child(DateSelected).setValue(editText.getText().toString());
//                String houseName = houseNo.toString() +" - " + agent.toString();
//
//                if(!houseName.isEmpty()){
//                    databaseReference.child(DateSelected).push().setValue(houseName);
//                    Log.d("BookingPage", "Booking uploaded: " + houseName + " on " + DateSelected);
//                    calendarClicked();
//                }
//            }
//
//        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String houseName = chooseHouse.getText().toString(); // get the house name from the text view
                databaseReference.child(DateSelected).push().setValue(houseName) // store the house name in the database
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(BookingPage.this, "Booking added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(BookingPage.this, "Booking failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

        private void calendarClicked() {
        databaseReference.child(DateSelected).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> eventList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String houseName = snapshot.getValue(String.class);
                    if (houseName != null) {
                        eventList.add(houseName);
                    }
                }
                if (eventList.size() > 0) {
                    ListView listView = findViewById(R.id.listView);


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(BookingPage.this, R.layout.center_text_list_item, eventList) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView textView = (TextView) view.findViewById(android.R.id.text1);
                            textView.setText((position + 1) + ". " + textView.getText());
                            return view;
                        }
                    };


                    listView.setAdapter(adapter);
                    chooseDate.setText(DateSelected + " : " + eventList.size() + " Booked");

                    // Add long click listener to ListView items
                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            // Handle long click event here
                            String houseName = eventList.get(position);
                            deleteEvent(houseName);
                            return true;
                        }
                    });

                } else {
                    ListView listView = findViewById(R.id.listView);
                    listView.setAdapter(null);
                    chooseDate.setText(DateSelected + ": No booking");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

        private void deleteEvent(String houseName) {
            DatabaseReference eventRef = databaseReference.child(DateSelected);
            eventRef.orderByValue().equalTo(houseName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        // Prompt user for confirmation
                        AlertDialog.Builder builder = new AlertDialog.Builder(BookingPage.this);
                        builder.setTitle("Delete booking?");
                        builder.setMessage("Are you sure you want to delete this section?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove event from database
                                eventSnapshot.getRef().removeValue();
                                dialog.dismiss();
                                Toast.makeText(BookingPage.this, "booking deleted", Toast.LENGTH_SHORT).show();
                                calendarClicked();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }
}