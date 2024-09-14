package com.starsky.dollarwallpaper.SetWallpaper;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.starsky.dollarwallpaper.R;
import com.starsky.dollarwallpaper.databinding.ActivitySetWallpaperBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SetWallpaperActivity extends AppCompatActivity {

    ActivitySetWallpaperBinding binding;
    String imageUrl;
    FirebaseFirestore database;
    FirebaseAuth auth;
    InterstitialAd mInterstitialAd;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        // Initialize Mobile Ads SDK
        MobileAds.initialize(this, initializationStatus -> {});

        // Load the interstitial ad
//        loadInterstitialAd();

//        new Handler().postDelayed(this::showInterstitialAd, 5000);

        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.setWallCat.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.GONE);

        Intent intent = getIntent();

        imageUrl = intent.getStringExtra("image");


        // Load image using Glide
        Glide.with(this)
                .load(imageUrl)
                .into(binding.setImage);


        binding.ApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AsyncTask to download and set wallpaper
                // new SetWallpaperTask().execute(image);

                binding.setWallCat.setVisibility(View.VISIBLE);
                binding.cancelButton.setVisibility(View.VISIBLE);

            }
        });

        //set Desktop
        binding.setDesktop.setOnClickListener(v -> {
            setWallpaper(WallpaperManager.FLAG_SYSTEM);
            binding.setWallCat.setVisibility(View.GONE);
            binding.cancelButton.setVisibility(View.GONE);
        });

        //set Lock Screen
        binding.setLock.setOnClickListener(v -> {
            setWallpaper(WallpaperManager.FLAG_LOCK);
            binding.setWallCat.setVisibility(View.GONE);
            binding.cancelButton.setVisibility(View.GONE);
        });

        // set Both
        binding.setBoth.setOnClickListener(v -> {
            setWallpaper(WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
            binding.setWallCat.setVisibility(View.GONE);
            binding.cancelButton.setVisibility(View.GONE);
        });

        //cancel
        binding.cancelButton.setOnClickListener(v -> {
            binding.setWallCat.setVisibility(View.GONE);
            binding.cancelButton.setVisibility(View.GONE);
        });

        binding.likeBtn.setOnClickListener(v -> {
            saveImageToFavorites(imageUrl);
        });

    }

    // Load Interstitial Ad
    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.d("AdMob", "Interstitial ad loaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d("AdMob", "Ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
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
            mInterstitialAd.show(this);
        } else {
            Log.d("AdMob", "The interstitial ad wasn't ready yet.");
        }
    }

    //favorites
    private void saveImageToFavorites(String imageUrl) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        String userId = Objects.requireNonNull(auth.getUid());

        // Reference to the user's favorites collection
        CollectionReference favoritesRef = database.collection("users").document(userId).collection("favorites");

        // Query to check if the image URL already exists in favorites
        favoritesRef.whereEqualTo("imageUrl", imageUrl).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // Image is already in favorites
                            Toast.makeText(getApplicationContext(), "Image is already in favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            // Image is not in favorites, add it
                            Map<String, Object> data = new HashMap<>();
                            data.put("imageUrl", imageUrl);

                            favoritesRef.add(data)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getApplicationContext(), "Image added to favorites", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), "Failed to add image to favorites", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Error occurred while checking the database
                        Toast.makeText(getApplicationContext(), "Failed to check if image is already in favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Set DLB
    private void setWallpaper(int flag) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                        try {
                            wallpaperManager.setBitmap(resource, null, true, flag);
                            Toast.makeText(getApplicationContext(), "Wallpaper set successfully!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.d("error", "" + e);
                            Toast.makeText(getApplicationContext(), "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Do nothing
                    }
                });
    }

}
