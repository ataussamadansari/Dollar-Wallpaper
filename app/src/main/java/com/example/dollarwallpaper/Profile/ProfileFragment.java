package com.example.dollarwallpaper.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.dollarwallpaper.Login_SignUp.UserModelClass;
import com.example.dollarwallpaper.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    ArrayList<CollectionClass> collectionClasses = new ArrayList<>();
    CollectionAdapter adapter;
    FirebaseFirestore database;
    FirebaseAuth auth;
    FragmentProfileBinding binding;
    UserModelClass userModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        collectedImage();

        // profile set
        database.collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userModel = documentSnapshot.toObject(UserModelClass.class);

//                        Glide.with(getContext()).load(userModel.getUser_profile()).into(binding.profileImage);
                        assert userModel != null;
                        binding.userName.setText(String.valueOf(userModel.getName()));
                        binding.userEmail.setText(String.valueOf(userModel.getEmail()));
                        binding.userPhone.setText(String.valueOf(userModel.getPhone()));
                    }
                });

        return view;
    }

    // Retrieve category names from Firestore
    private void collectedImage() {
        database.collection("users")
                .document(Objects.requireNonNull(auth.getUid()))
                .collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    collectionClasses.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        if (imageUrl != null) {
                            collectionClasses.add(new CollectionClass(imageUrl));
                        }
                    }
                    setAdapter();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void setAdapter() {
        binding.collectionRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new CollectionAdapter(getContext(), collectionClasses);
        binding.collectionRV.setAdapter(adapter);

    }
}