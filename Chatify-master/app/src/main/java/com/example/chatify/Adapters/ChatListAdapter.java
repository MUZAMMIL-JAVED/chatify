package com.example.chatify.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatify.R;
import com.example.chatify.Models.UserModel;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserModel> list;
    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(UserModel user);
    }

    public ChatListAdapter(Context context, ArrayList<UserModel> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_contact, parent, false);
        view.setBackgroundColor(Color.TRANSPARENT);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.MyViewHolder holder, int position) {
        UserModel admin = list.get(position);
        holder.name.setText(admin.getName());
        //holder.email.setText(admin.getEmail());
        //holder.number.setText(admin.getNumber());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(admin));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, number;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtReceiverName);
            /*email = itemView.findViewById(R.id.txtReceiverEmail);
            number = itemView.findViewById(R.id.txtReceiverNumber);*/

        }
    }
}