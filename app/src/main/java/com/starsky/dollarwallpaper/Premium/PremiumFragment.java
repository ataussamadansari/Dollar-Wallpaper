package com.starsky.dollarwallpaper.Premium;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.starsky.dollarwallpaper.R;
import com.starsky.dollarwallpaper.SetWallpaper.SetWallpaperActivity;
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
    InterstitialAd mInterstitialAd;
    Intent intent;

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

        // Initialize Mobile Ads SDK
        MobileAds.initialize(getContext(), initializationStatus -> {});

        // Load the interstitial ad
        loadInterstitialAd();

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
        postAdapter = new PostAdapter(getContext(), uploadModelClasses, this::onItemClick);
        binding.premiumRV.setAdapter(postAdapter);
    }

    // Handle item clicks from the adapter
    public void onItemClick(UploadModelClass modelClass) {
        // Handle click event here
        intent = new Intent(getContext(), SetWallpaperActivity.class);
        intent.putExtra("image", modelClass.getImageURL());
        showInterstitialAd();
    }

    // Load Interstitial Ad
    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(getContext(), getString(R.string.interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.d("AdMob", "Interstitial ad loaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                startActivity(intent);
                                Log.d("AdMob", "Ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                startActivity(intent);
                                Log.d("AdMob", "Ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                mInterstitialAd = null; // Ad is shown, set it to null to prevent multiple use
                                Log.d("AdMob", "Ad showed fullscreen content.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                        Log.d("AdMob", "Failed to load interstitial ad: " + loadAdError.getMessage());
                    }
                });
    }

    // Show Interstitial Ad
    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) getContext());
        } else {
            Log.d("AdMob", "The interstitial ad wasn't ready yet.");
            startActivity(intent);
        }
    }

}