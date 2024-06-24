package com.example.admindollar.Category;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.admindollar.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewHolder> {

    Context context;
    ArrayList<CategoryClass> categoryClasses;

    public CategoryAdapter(Context context, ArrayList<CategoryClass> categoryClasses) {
        this.context = context;
        this.categoryClasses = categoryClasses;
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


    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView catName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            catName = itemView.findViewById(R.id.show_cat_name);
        }
    }
}
