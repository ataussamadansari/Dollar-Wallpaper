package com.example.admindollar.ShowUsers;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.admindollar.Category.CategoryClass;
import com.example.admindollar.R;
import com.example.admindollar.databinding.ActivityShowUsersBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Objects;

public class ShowUsersActivity extends AppCompatActivity {

    ArrayList<ShowUserClass> showUserClasses = new ArrayList<>();
    ShowUserAdapter adapter;
    FirebaseFirestore database;
    FirebaseAuth auth;
    ActivityShowUsersBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        showUserData();

    }
    //back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //access all users data
    private void showUserData() {
        database.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    showUserClasses.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        ShowUserClass showUserClass = documentSnapshot.toObject(ShowUserClass.class);
                        showUserClasses.add(showUserClass);
                    }
                    setAdapter();
                })
                .addOnFailureListener(e -> {
                    // Handle onCancelled event
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }


    //set Adapter
    private void setAdapter() {
        binding.showUserRV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShowUserAdapter(this, showUserClasses);
        binding.showUserRV.setAdapter(adapter);
    }
}