package com.starsky.admindollar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.starsky.admindollar.Category.CategoryAdapter;
import com.starsky.admindollar.Category.CategoryClass;
import com.starsky.admindollar.ShowUsers.ShowUsersActivity;
import com.starsky.admindollar.WallpaperPost.ImageActivity;
import com.starsky.admindollar.databinding.ActivityMainBinding;
import com.starsky.admindollar.premium.PremiumActivity;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore database;
    ActivityMainBinding binding;
    ProgressDialog progressDialog;
    Dialog create_cat_dialog, show_cat_dialog;
    private Uri selectedImageUri;
    ArrayList<CategoryClass> categoryClasses = new ArrayList<>();
    CategoryAdapter adapter;
    EditText cat_name, show_cat_name;
    Button create_cat_btn;
    ImageView close_btn;
    RecyclerView catRV;

    @SuppressLint({"UseCompatLoadingForDrawables", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Get a reference to the Firestore database
        database = FirebaseFirestore.getInstance();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu));


        // Initialize click listeners
        binding.uploadIV.setOnClickListener(v -> openGallery());
        binding.uploadBtn.setOnClickListener(v -> {
            uploadData();
            binding.uploadIV.setImageResource(R.drawable.upload_img);
        });

        binding.premiumUploadBtn.setOnClickListener(v -> {
            premiumUploadData();
            binding.uploadIV.setImageResource(R.drawable.upload_img);
        });


        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCancelable(false);

    }

    // Open gallery to select an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        resultLauncher.launch(intent);
    }


    // Activity result launcher for picking image from gallery
    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.uploadIV.setImageURI(selectedImageUri);
                }
            });


    // Upload image and title to Firebase
    private void uploadData() {
        if (selectedImageUri != null) {
            // Show ProgressDialog
            progressDialog.show();
            uploadImage(selectedImageUri);

        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void premiumUploadData() {
        if (selectedImageUri != null) {
            // Show ProgressDialog
            progressDialog.show();
            premiumUploadImage(selectedImageUri);

        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveImageDataToFirestore(imageUrl); // After image upload, save data to Firestore
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void premiumUploadImage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("premium-images/" + UUID.randomUUID().toString());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveImageDataToFirestorePremium(imageUrl); // After image upload, save data to Firestore
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveImageDataToFirestore(String imageUrl) {
        // Create a new document in the "images" collection with a generated ID
        database.collection("images")
                .add(new UploadModelClass(imageUrl))
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    Toast.makeText(this, "Failed to upload data", Toast.LENGTH_SHORT).show();
                });
    }
    private void saveImageDataToFirestorePremium(String imageUrl) {
        // Create a new document in the "images" collection with a generated ID
        database.collection("premiumImages")
                .add(new UploadModelClass(imageUrl))
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    Toast.makeText(this, "Failed to upload data", Toast.LENGTH_SHORT).show();
                });
    }


    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.show_image_menu) {
            startActivity(new Intent(this, ImageActivity.class));
            return true;
        } else if (id == R.id.show_image_menu_pre) {
            startActivity(new Intent(this, PremiumActivity.class));
            return true;
        } else if (id == R.id.profile_menu) {
            startActivity(new Intent(this, ShowUsersActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}