package com.example.admindollar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
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

import com.example.admindollar.Category.CategoryAdapter;
import com.example.admindollar.Category.CategoryClass;
import com.example.admindollar.ShowUsers.ShowUsersActivity;
import com.example.admindollar.WallpaperPost.ImageActivity;
import com.example.admindollar.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;
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

        //create category
        create_cat_dialog = new Dialog(this);
        create_cat_dialog.setContentView(R.layout.create_cat_dialog);
        Objects.requireNonNull(create_cat_dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        create_cat_dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dia_bg));
        create_cat_dialog.setCancelable(false);

        cat_name = create_cat_dialog.findViewById(R.id.cat_name);
        create_cat_btn = create_cat_dialog.findViewById(R.id.cat_btn);
        close_btn = create_cat_dialog.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(v -> create_cat_dialog.dismiss());

        create_cat_btn.setOnClickListener(v -> {
            String categoryName = cat_name.getText().toString().trim();
            if (categoryName.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            } else {
                uploadCategory(categoryName);
            }
        });

        //show category
        show_cat_dialog = new Dialog(this);
        show_cat_dialog.setContentView(R.layout.show_cat_dialog);
        Objects.requireNonNull(show_cat_dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        show_cat_dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dia_bg));
        show_cat_dialog.setCancelable(false);

        show_cat_name = show_cat_dialog.findViewById(R.id.show_cat_name);
        create_cat_btn = show_cat_dialog.findViewById(R.id.cat_btn);
        close_btn = show_cat_dialog.findViewById(R.id.close_btn);
        catRV = show_cat_dialog.findViewById(R.id.categoryRV);
        close_btn.setOnClickListener(v -> show_cat_dialog.dismiss());

        retrieveCategoryNames();

        // Initialize click listeners
        binding.uploadIV.setOnClickListener(v -> openGallery());
        binding.uploadBtn.setOnClickListener(v -> {
            uploadData();
            binding.decTV.setText(null);
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
        if (selectedImageUri != null && binding.decTV.getText() != null) {
            binding.decTV.append(", All");
            String title = binding.decTV.getText().toString();
            if (!title.isEmpty()) {
                // Show ProgressDialog
                progressDialog.show();
                uploadImage(selectedImageUri, title);
            } else {
                Toast.makeText(this, "Please provide a title", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri imageUri, final String title) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveImageDataToFirestore(imageUrl, title); // After image upload, save data to Firestore
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveImageDataToFirestore(String imageUrl, String title) {
        // Create a new document in the "images" collection with a generated ID
        database.collection("images")
                .add(new UploadModelClass(imageUrl, title))
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss(); // Dismiss ProgressDialog
                    Toast.makeText(this, "Failed to upload data", Toast.LENGTH_SHORT).show();
                });
    }


    // Update uploadCategory method to use Firestore
    private void uploadCategory(String categoryName) {
        // Add a new document with a generated ID
        database.collection("categories")
                .add(new CategoryClass(categoryName))
                .addOnSuccessListener(documentReference -> {
                    // Category uploaded successfully
                    Toast.makeText(MainActivity.this, "Category uploaded successfully", Toast.LENGTH_SHORT).show();
                    cat_name.setText("");
                    create_cat_dialog.dismiss(); // Dismiss the dialog after successful upload
                })
                .addOnFailureListener(e -> {
                    // Handle failures
                    Toast.makeText(MainActivity.this, "Failed to upload category", Toast.LENGTH_SHORT).show();
                });
    }

    // Update retrieveCategoryNames method to use Firestore
    private void retrieveCategoryNames() {
        database.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryClasses.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        CategoryClass category = documentSnapshot.toObject(CategoryClass.class);
                        categoryClasses.add(category);
                    }
                    setCategoryAdapter();
                })
                .addOnFailureListener(e -> {
                    // Handle onCancelled event
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setCategoryAdapter() {
        // Create adapter with retrieved category names and set it to the RecyclerView
        catRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new CategoryAdapter(getApplicationContext(), categoryClasses);
        catRV.setAdapter(adapter);
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
        if (id == R.id.cat_menu) {
            create_cat_dialog.show();
            return true;
        } else if (id == R.id.show_cat_menu) {
            show_cat_dialog.show();
            return true;
        } else if (id == R.id.show_image_menu) {
            startActivity(new Intent(this, ImageActivity.class));
            return true;
        } else if (id == R.id.profile_menu) {
            startActivity(new Intent(this, ShowUsersActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}