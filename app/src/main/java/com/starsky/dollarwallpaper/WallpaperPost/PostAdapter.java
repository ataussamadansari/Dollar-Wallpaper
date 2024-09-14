package com.starsky.dollarwallpaper.WallpaperPost;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.starsky.dollarwallpaper.R;
import com.starsky.dollarwallpaper.SetWallpaper.SetWallpaperActivity;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {
    Context context;
    ArrayList<UploadModelClass> uploadModelClasses;

    public PostAdapter(Context context, ArrayList<UploadModelClass> uploadModelClasses) {
        this.context = context;
        this.uploadModelClasses = uploadModelClasses;
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SetWallpaperActivity.class);
            intent.putExtra("image", modelClass.getImageURL());
            context.startActivity(intent);
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
