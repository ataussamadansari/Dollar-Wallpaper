package com.starsky.dollarwallpaper.WallpaperPost;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.starsky.dollarwallpaper.R;
import com.starsky.dollarwallpaper.SetWallpaper.SetWallpaperActivity;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {
    Context context;
    ArrayList<UploadModelClass> uploadModelClasses;
    OnItemClickListener onItemClickListener; // Interface for click listener

    // Define interface for item clicks
    public interface OnItemClickListener {
        void onItemClick(UploadModelClass modelClass);
    }

    // Constructor with listener
    public PostAdapter(Context context, ArrayList<UploadModelClass> uploadModelClasses, OnItemClickListener listener) {
        this.context = context;
        this.uploadModelClasses = uploadModelClasses;
        this.onItemClickListener = listener;
    }


    @NonNull
    @Override
    public PostAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.post_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.viewHolder holder, int position) {
        UploadModelClass modelClass = uploadModelClasses.get(position);

        // Set aspect ratio to 16:9
        /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.5625); // Calculating 16:9 aspect ratio
        holder.image.setLayoutParams(layoutParams);*/

        // Load image using Glide
        Glide.with(context)
                .load(modelClass.getImageURL())
                .centerCrop()
                .into(holder.image);


        holder.desc.setText(modelClass.getDesc());
        //Glide.with(context).load(modelClass.getImage()).into(holder.image);

        // Set item click listener
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(modelClass); // Pass clicked item data
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadModelClasses.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(ArrayList<UploadModelClass> filteredPosts) {
        uploadModelClasses = filteredPosts;
        notifyDataSetChanged();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView desc;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.post_img);
            desc = itemView.findViewById(R.id.description);
            desc.setVisibility(View.GONE);
        }
    }

}
