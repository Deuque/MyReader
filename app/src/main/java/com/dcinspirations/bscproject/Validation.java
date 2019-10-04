package com.dcinspirations.bscproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dcinspirations.bscproject.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.droidsonroids.gif.GifImageView;


public class Validation extends AppCompatActivity {
    EditText name,status,pass;
    RelativeLayout bt;
    String selection;
    TextView at;
    GifImageView giv;
    Toolbar tb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);
        TabLayout tabs = findViewById(R.id.tabs);
        tb = findViewById(R.id.toolbar);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selection = "login";
        name = findViewById(R.id.name);
        status = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        at = findViewById(R.id.at);
        giv = findViewById(R.id.lgif);
        bt = findViewById(R.id.action);

        String state = getIntent().getExtras().getString("state");
        if(state.equalsIgnoreCase("login")){
            tabs.addTab(tabs.newTab().setText("Login"),true);
            tabs.addTab(tabs.newTab().setText("Register"));
            selection = "login";
        }else{
            tabs.addTab(tabs.newTab().setText("Login"));
            tabs.addTab(tabs.newTab().setText("Register"),true);
            name.setVisibility(View.VISIBLE);
            selection = "register";
        }



        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selection.equalsIgnoreCase("login")) {

                    SignIn();
                }else{

                    SignUp();
                }
            }
        });
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().toString().equalsIgnoreCase("Login")){
                    name.setVisibility(View.GONE);
                    at.setText("Login");
                    selection = "Login";
                    giv.setVisibility(View.INVISIBLE);
                }else{
                    name.setVisibility(View.VISIBLE);
                    at.setText("Register");
                    selection = "Register";
                    giv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    private void SignIn(){
        String emtext = status.getText().toString().trim();
        String ptext = pass.getText().toString().trim();
        if(emtext.isEmpty()){
            status.requestFocus();
            status.setError("Enter Username");
            return;
        }
        if(ptext.isEmpty()){
            pass.requestFocus();
            pass.setError("Enter Password");
            return;
        }

        at.setText("Logging In...");
        giv.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(emtext,ptext)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users");
                            dbref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    UserDetails ud = dataSnapshot.getValue(UserDetails.class);
                                    new Sp().setLoggedIn(true);
                                    new Sp().setUInfo(dataSnapshot.getKey(),ud.name,ud.status);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }else{
                            Toast.makeText(Validation.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            giv.setVisibility(View.INVISIBLE);
                            at.setText("Login");
                        }
                    }
                });
    }
    private void SignUp(){
        final String emtext = status.getText().toString().trim();
        String ptext = pass.getText().toString().trim();
        final String ntext = name.getText().toString().trim();
        if(ntext.isEmpty()){
            name.requestFocus();
            name.setError("Enter Name");
            return;
        }
        if(emtext.isEmpty()){
            status.requestFocus();
            status.setError("Enter Status");
            return;
        }
        if(ptext.isEmpty()){
            pass.requestFocus();
            pass.setError("Enter Password");
            return;
        }
        at.setText("Creating User...");
        giv.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emtext,ptext)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users");
                            dbref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("name").setValue(ntext);
                            dbref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("status").setValue(emtext);
                            new Sp().setLoggedIn(true);
                            new Sp().setUInfo(FirebaseAuth.getInstance().getCurrentUser().getUid(),ntext,emtext);
                            finish();
                        }else{
                            Toast.makeText(Validation.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            giv.setVisibility(View.INVISIBLE);
                            at.setText("Register");
                        }
                    }
                });
    }

}