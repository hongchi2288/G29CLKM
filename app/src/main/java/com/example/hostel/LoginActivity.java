package com.example.hostel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText name,password;
    Button login;
    Spinner usertype;
    FirebaseAuth auth;
    ImageView  ButtonBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name= findViewById((R.id.name));
        password= findViewById(R.id.password);
        login=findViewById(R.id.login);
        auth=FirebaseAuth.getInstance();
        ButtonBack=findViewById(R.id.buttonBack);

        ButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateEmail()|!validatePassword()){

                }else
                    checkUser();
            }
        });
    }

    //Validate the password and email
    public boolean validatePassword(){
        String val=password.getText().toString();
        if (val.isEmpty()){
            password.setError("Password cant empty");
            return false;
        }else{
            password.setError(null);
            return true;
        }
    }
    public boolean validateEmail(){
        String val=name.getText().toString();
        if (val.isEmpty()){
            name.setError("Cant empty");
            return false;
        }else{
            name.setError(null);
            return true;
        }
    }
    //Check the user account exist in database
    private void checkUser(){
        String txt_name=name.getText().toString().trim();
        String txt_password=password.getText().toString().trim();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("user");
        Query checkUserDatabase =reference.orderByChild("name").equalTo(txt_name);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    name.setError(null);
                    String passwordfromDB=snapshot.child(txt_name).child("password").getValue(String.class);


                    if (passwordfromDB.equals(txt_password)) {
                        name.setError(null);
                        String namefromDB=snapshot.child(txt_name).child("name").getValue(String.class);
                        String emailfromDB=snapshot.child(txt_name).child("email").getValue(String.class);
                        Intent intent;
                            intent = new Intent(LoginActivity.this, HomePageActivity.class);
                            intent.putExtra("email",emailfromDB);
                            intent.putExtra("name",namefromDB);
                            intent.putExtra("password",passwordfromDB);
                            startActivity(intent);


                    }else {
                        password.setError("Invalid Credentials");
                        password.requestFocus();
                    }
                }else{
                    name.setError("Invalid Credentials");
                    name.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}