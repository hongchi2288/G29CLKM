package com.example.hostel;


import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText email;
    EditText name;
    EditText password;
    Button Register;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    ImageView ButtonBack;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email= findViewById(R.id.email);
        name=findViewById((R.id.Name));
        password=findViewById(R.id.password);
        Register=findViewById(R.id.Register);
        auth=FirebaseAuth.getInstance();
        ButtonBack=findViewById(R.id.buttonBack);

        ButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email=email.getText().toString();
                String txt_name=name.getText().toString();
                String txt_password=password.getText().toString();

                database= FirebaseDatabase.getInstance();
                reference=database.getReference("user");

                //store data
                HelperClass helperClass=new HelperClass(txt_email,txt_name,txt_password);
                reference.child(txt_name).setValue(helperClass);
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);



                //Checking the element valid
                if(TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_name)||TextUtils.isEmpty(txt_password)){
                    Toast.makeText(RegisterActivity.this,"Empty credentials!",Toast.LENGTH_SHORT).show();
                }else if (txt_password.length()<6){
                    Toast.makeText(RegisterActivity.this,"Password too short!",Toast.LENGTH_SHORT).show();
                }else{

                    registerUser(txt_email,txt_name,txt_password);
                }
            }
        });
    }
    private void registerUser(String email,String name,String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity
                .this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Register user successful!", LENGTH_SHORT).show();
                    Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegisterActivity.this,"Register failed!", LENGTH_SHORT).show();
                }
            }

        });

    }

}