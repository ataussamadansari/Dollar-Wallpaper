package com.example.dollarwallpaper.MainPage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.dollarwallpaper.Category.CategoryAdapter;
import com.example.dollarwallpaper.Category.CategoryClass;
import com.example.dollarwallpaper.WallpaperPost.PostAdapter;
import com.example.dollarwallpaper.WallpaperPost.UploadModelClass;
import com.example.dollarwallpaper.databinding.FragmentMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    // Declare ProgressDialog variable
    private ProgressDialog progressDialog;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;
    ArrayList<CategoryClass> categoryClasses = new ArrayList<>();
    ArrayList<UploadModelClass> uploadModelClasses = new ArrayList<>();
    CategoryAdapter adapter;
    PostAdapter adapter1;
    FirebaseFirestore database;
    private MainViewModel viewModel;
    FragmentMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        database = FirebaseFirestore.getInstance();

        binding.categoryRV.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        binding.categoryRV.setLayoutManager(RecyclerViewLayoutManager);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        setupViewModelObservers();

        return view;
    }

    // Function to show progress dialog
    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    // Function to dismiss progress dialog
    private void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retrieveCategoryNames(); // Retrieve category names
        populatePostList(); // Populate post list
        setPostAdapter();
    }


    private void setupViewModelObservers() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<ArrayList<CategoryClass>>() {
            @Override
            public void onChanged(ArrayList<CategoryClass> categories) {
                // Update category RecyclerView adapter when data changes
                setCategoryAdapter();
            }
        });

        viewModel.getUploadModelClasses().observe(getViewLifecycleOwner(), new Observer<ArrayList<UploadModelClass>>() {
            @Override
            public void onChanged(ArrayList<UploadModelClass> uploadModelClasses) {
                // Update post RecyclerView adapter when data changes
                setPostAdapter();
                populatePostList();
            }
        });
    }


    // Retrieve category names from Firestore
    private void retrieveCategoryNames() {
        showProgressDialog();
        database.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dismissProgressDialog();
                    categoryClasses.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String categoryName = documentSnapshot.getString("catName");
                        if (categoryName != null) {
                            categoryClasses.add(new CategoryClass(categoryName));
                        }
                    }
                    setCategoryAdapter();
                })
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Retrieve post data from Firestore
    private void populatePostList() {
        showProgressDialog();
        database.collection("images")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dismissProgressDialog();
                    uploadModelClasses.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        UploadModelClass upload = documentSnapshot.toObject(UploadModelClass.class);
                        uploadModelClasses.add(upload);
                    }
                    setPostAdapter();
                })
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    //retrieveCategoryName
    /*private void retrieveCategoryNames() {
        // Show progress dialog before fetching data
        showProgressDialog();
        // Access your Firebase Realtime Database or any other data source
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories");

        // Add a listener to retrieve category names
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Dismiss progress dialog after retrieving data
                dismissProgressDialog();
                // Clear existing categoryClasses list
                categoryClasses.clear();

                // Iterate through dataSnapshot to retrieve category names
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String categoryName = snapshot.getValue(String.class);
                    // Create CategoryClass object for each category name and add to the list
                    categoryClasses.add(new CategoryClass(categoryName));
                }

                // Set adapter for category RecyclerView
                setCategoryAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
                Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                // Dismiss progress dialog in case of error
                dismissProgressDialog();
            }
        });
    }*/

    private void setCategoryAdapter() {
        // Create adapter with retrieved category names and set it to the RecyclerView
        adapter = new CategoryAdapter(getContext(), categoryClasses, categoryName -> {
            // Handle category click event if needed
            Log.d("Cat", categoryName);
            updatePostRecyclerView(categoryName);
        });
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.categoryRV.setLayoutManager(HorizontalLayout);
        binding.categoryRV.setAdapter(adapter);
    }

    //populatePostList
   /* private void populatePostList() {
        // Show progress dialog before fetching data
        showProgressDialog();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("images");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Dismiss progress dialog after retrieving data
                dismissProgressDialog();
                uploadModelClasses.clear(); // Clear the existing list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UploadModelClass upload = snapshot.getValue(UploadModelClass.class);
                    if (upload != null) {
                        uploadModelClasses.add(new UploadModelClass(upload.getImageURL(), upload.getDesc()));
                    }
                }
                // After retrieving data from Firebase, set the adapter for RecyclerView
                setPostAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                // Dismiss progress dialog after retrieving data
                dismissProgressDialog();
            }
        });
    }*/


    private void setPostAdapter() {
        binding.postRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter1 = new PostAdapter(getContext(), uploadModelClasses);
        binding.postRV.setAdapter(adapter1);
    }



    private void updatePostRecyclerView(String categoryName) {
        ArrayList<UploadModelClass> filteredPosts = new ArrayList<>();
        String categoryToSearch = categoryName.toLowerCase(); // Convert the search query to lowercase

        for (UploadModelClass post : uploadModelClasses) {
            // Convert the category name to lowercase for comparison
            String postCategory = post.getDesc().toLowerCase();

            // Check if the post matches the selected category or if the category is "All"
            if (postCategory.equals(categoryToSearch) || categoryToSearch.equals("all")) {
                // If the post matches the category, add it to the filtered list
                filteredPosts.add(post);
            } else {
                // If the category doesn't match, check if the description or tags contain the category name
                if (postCategory.contains(categoryToSearch)) {
                    filteredPosts.add(post);
                } else {
                    // If the category name is not found in the description, check the tags
                    String[] tags = postCategory.split(", ");
                    for (String tag : tags) {
                        if (tag.equals(categoryToSearch)) {
                            filteredPosts.add(post);
                            break; // Break the loop once the post is added to avoid duplicates
                        }
                    }
                }
            }
        }
        // Update the adapter with filtered posts
        adapter1.updateData(filteredPosts);
    }

}
