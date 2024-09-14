package com.starsky.dollarwallpaper.MainPage;

import android.app.ProgressDialog;
import android.os.Bundle;
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

import com.starsky.dollarwallpaper.WallpaperPost.PostAdapter;
import com.starsky.dollarwallpaper.WallpaperPost.UploadModelClass;
import com.starsky.dollarwallpaper.databinding.FragmentMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    // Declare ProgressDialog variable
    private ProgressDialog progressDialog;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    ArrayList<UploadModelClass> uploadModelClasses = new ArrayList<>();
    PostAdapter postAdapter;
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
        populatePostList(); // Populate post list
        setPostAdapter();
    }

    private void setupViewModelObservers() {
        viewModel.getUploadModelClasses().observe(getViewLifecycleOwner(), new Observer<ArrayList<UploadModelClass>>() {
            @Override
            public void onChanged(ArrayList<UploadModelClass> uploadModelClasses) {
                // Update post RecyclerView adapter when data changes
                setPostAdapter();
                populatePostList();
            }
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

    private void setPostAdapter() {
        binding.postRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        postAdapter = new PostAdapter(getContext(), uploadModelClasses);
        binding.postRV.setAdapter(postAdapter);
    }

}
