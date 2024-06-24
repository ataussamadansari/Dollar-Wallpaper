package com.example.dollarwallpaper.SetWallpaper;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dollarwallpaper.databinding.ActivitySetWallpaperBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
    private Bitmap selectedBitmap;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        WallpaperManager wallpaperManager;
        wallpaperManager = WallpaperManager.getInstance(this);

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

       /* Glide.with(this)
                .load(imageUrl)
                .into(binding.setImage);*/

//        Glide.with(this).load(image).into(binding.setImage);

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
    //favorites

    private void saveImageToFavorites(String imageUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUrl);

        // Add the data to Firestore
        database.collection("users")
                .document(Objects.requireNonNull(auth.getUid()))
                .collection("favorites")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Image added to favorites", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to add image to favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*private class SetWallpaperTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                Log.e(TAG, "Error downloading image: " + e.getMessage());
                return null;
            }
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    wallpaperManager.setBitmap(bitmap); // Set as both lock screen and home screen wallpaper
                    Toast.makeText(getApplicationContext(), "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e(TAG, "Error setting wallpaper: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
            }
        }
    }*/


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
