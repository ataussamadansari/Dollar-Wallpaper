package com.example.dollarwallpaper.Premium;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.dollarwallpaper.MainPage.MainViewModel;
import com.example.dollarwallpaper.WallpaperPost.PostAdapter;
import com.example.dollarwallpaper.WallpaperPost.UploadModelClass;
import com.example.dollarwallpaper.databinding.FragmentPremiumBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PremiumFragment extends Fragment {
    // Declare ProgressDialog variable
    private ProgressDialog progressDialog;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    ArrayList<UploadModelClass> uploadModelClasses = new ArrayList<>();
    PostAdapter adapter1;
    FirebaseFirestore database;
    FragmentPremiumBinding binding;
    private MainViewModel mainViewModel;

    public PremiumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPremiumBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        database = FirebaseFirestore.getInstance();

        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


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
        populatePostList(); // Populate post list
        setPostAdapter();
    }



    // Retrieve post data from Firestore
    private void populatePostList() {
        showProgressDialog();
        database.collection("premium-Images")
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


    private void setPostAdapter() {
        binding.premiumRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter1 = new PostAdapter(getContext(), uploadModelClasses);
        binding.premiumRV.setAdapter(adapter1);
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