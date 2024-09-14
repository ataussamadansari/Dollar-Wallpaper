package com.starsky.dollarwallpaper.Category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starsky.dollarwallpaper.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewHolder> {

    Context context;
    ArrayList<CategoryClass> categoryClasses;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryName);
    }

    public CategoryAdapter(Context context, ArrayList<CategoryClass> categoryClasses, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryClasses = categoryClasses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.category_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CategoryClass categoryClass = categoryClasses.get(position);
        holder.catName.setText(categoryClass.getCatName());
    }

    @Override
    public int getItemCount() {
        return categoryClasses.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView catName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            catName = itemView.findViewById(R.id.cat_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String categoryName = categoryClasses.get(position).getCatName();
                        listener.onCategoryClick(categoryName);
                    }
                }
            });
        }

    }
}
