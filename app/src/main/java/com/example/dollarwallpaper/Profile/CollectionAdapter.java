package com.example.dollarwallpaper.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dollarwallpaper.R;

import java.util.ArrayList;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.viewHolder> {
    Context context;
    ArrayList<CollectionClass> collectionClasses;

    public CollectionAdapter(Context context, ArrayList<CollectionClass> collectionClasses) {
        this.context = context;
        this.collectionClasses = collectionClasses;
    }

    @NonNull
    @Override
    public CollectionAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.post_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionAdapter.viewHolder holder, int position) {
        CollectionClass collectionClass = collectionClasses.get(position);
        Glide.with(context)
                .load(collectionClass.getImageUrl())
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return collectionClasses.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.post_img);
        }
    }
}
