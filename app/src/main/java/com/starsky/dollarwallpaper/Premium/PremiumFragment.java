package com.starsky.dollarwallpaper.Premium;

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

import com.starsky.dollarwallpaper.MainPage.MainViewModel;
import com.starsky.dollarwallpaper.WallpaperPost.PostAdapter;
import com.starsky.dollarwallpaper.WallpaperPost.UploadModelClass;
import com.starsky.dollarwallpaper.databinding.FragmentPremiumBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PremiumFragment extends Fragment {
    // Declare ProgressDialog variable
    private ProgressDialog progressDialog;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    ArrayList<UploadModelClass> uploadModelClasses = new ArrayList<>();
    PostAdapter postAdapter;
    FirebaseFirestore database;
    FragmentPremiumBinding binding;

    private static final int UPI_PAYMENT_REQUEST_CODE = 123;


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
        database.collection("premiumImages")
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
        postAdapter = new PostAdapter(getContext(), uploadModelClasses);
        binding.premiumRV.setAdapter(postAdapter);
    }
}