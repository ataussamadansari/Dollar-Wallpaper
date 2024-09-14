package com.starsky.dollarwallpaper.Profile;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.starsky.dollarwallpaper.R;
import com.starsky.dollarwallpaper.SetWallpaper.SetWallpaperActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.viewHolder> {
    private Context context;
    FirebaseFirestore database;
    FirebaseAuth auth;
    private ArrayList<CollectionClass> collectionClasses;

    public CollectionAdapter(Context context, ArrayList<CollectionClass> collectionList) {
        this.context = context;
        this.collectionClasses = collectionList;
    }

    @NonNull
    @Override
    public CollectionAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.collection_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {
        CollectionClass collectionClass = collectionClasses.get(position);
        Glide.with(context)
                .load(collectionClass.getImageUrl())
                .centerCrop()
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the clicked image URL to the SetWallpaperActivity
                String imageUrl = collectionClass.getImageUrl();

                // Create an Intent to start the SetWallpaperActivity
                Intent intent = new Intent(context, SetWallpaperActivity.class);
                intent.putExtra("image", imageUrl);
                startActivity(context, intent, null);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the clicked image URL to the fragment
                String imageUrl = collectionClass.getImageUrl();

                // Remove the image from favorites
                removeFromFavorites(imageUrl, position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return collectionClasses.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, delete;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.post_img);
            delete = itemView.findViewById(R.id.deleteBtn);
        }
    }

    // Method to remove image from favorites
    public void removeFromFavorites(String imageUrl, int position) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        database.collection("users")
                .document(Objects.requireNonNull(auth.getUid()))
                .collection("favorites")
                .whereEqualTo("imageUrl", imageUrl)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                    Toast.makeText(context, "Image removed from favorites", Toast.LENGTH_SHORT).show();
                    // Remove the item from the collection list
                    collectionClasses.remove(position);
                    // Notify the adapter about the dataset change
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
