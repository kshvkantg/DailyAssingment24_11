package com.example.dailyassingment24_11;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.viewHolder> {
    Context context;
    ArrayList<ContactsInfo> contactsInfoArrayList;

    public MyCustomAdapter(Context context, ArrayList<ContactsInfo> contactsInfoArrayList) {
        this.context = context;
        this.contactsInfoArrayList = contactsInfoArrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_info,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.name.setText(contactsInfoArrayList.get(position).getDisplayName());
        holder.phoneNumber.setText(contactsInfoArrayList.get(position).getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return contactsInfoArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phoneNumber;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.displayName);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
        }
    }
}
