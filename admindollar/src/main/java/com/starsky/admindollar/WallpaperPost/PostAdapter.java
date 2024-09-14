package com.example.admindollar.WallpaperPost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.admindollar.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder>{

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
        Glide.with(context)
                .load(modelClass.getImageURL())
                .centerCrop()
                .into(holder.image);

        holder.desc.setText(modelClass.getDesc());
    }

    @Override
    public int getItemCount() {
        return uploadModelClasses.size();
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
