package com.starsky.admindollar.ShowUsers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starsky.admindollar.R;

import java.util.ArrayList;

public class ShowUserAdapter extends RecyclerView.Adapter<ShowUserAdapter.viewHolder> {

    Context context;
    ArrayList<ShowUserClass> showUserClasses;

    public ShowUserAdapter(Context context, ArrayList<ShowUserClass> showUserClasses) {
        this.context = context;
        this.showUserClasses = showUserClasses;
    }

    @NonNull
    @Override
    public ShowUserAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.show_users_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShowUserAdapter.viewHolder holder, int position) {
        ShowUserClass showUserClass = showUserClasses.get(position);
        holder.name.setText(showUserClass.getName());
        holder.email.setText(showUserClass.getEmail());
        holder.phone.setText(showUserClass.getPhone());
        holder.pass.setText(showUserClass.getPass());
    }

    @Override
    public int getItemCount() {
        return showUserClasses.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView name, email, phone, pass;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.userEmail);
            phone = itemView.findViewById(R.id.userPhone);
            pass = itemView.findViewById(R.id.userPass);
        }
    }
}
