package com.example.mysecret;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.MyViewHolder> {
    public ArrayList<Note> notesArrayList;
    private final RecyclerViewInterface recyclerViewInterface;

    //Constructor for our recycler view adapter
    public NotesRecyclerViewAdapter(ArrayList<Note> notesArrayList,RecyclerViewInterface recyclerViewInterface) {
        this.notesArrayList = notesArrayList;
        this.recyclerViewInterface= recyclerViewInterface;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Note> filterList){
        // below line is to add our filtered
        // list in our course array list.
        notesArrayList = filterList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }


    //My View Holder holds every item that inside of our recycler item view
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //Views that we are gonna hold
        private TextView title,notes;

        //Constructor
        public MyViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            title=itemView.findViewById(R.id.titleText);
            notes=itemView.findViewById(R.id.notesText);

            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface!=null) {
                   int pos=getAdapterPosition();

                   if (pos!=RecyclerView.NO_POSITION){
                       recyclerViewInterface.itemClick(pos);
                   }
                }

            });
        }
    }

    @NonNull
    @Override
    //Burada layoutumuzu MyViewHolderema ilişkilendırme ışlemını yapıyorum
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent,false);
        return new MyViewHolder(view,recyclerViewInterface);
    }

    @Override
    //Burada da layout ıcındekı ıtemlerı ılıskilendırıyoruz
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //position here represents that which element's index we pressed
        holder.title.setText(notesArrayList.get(position).getTitle());
        holder.notes.setText(notesArrayList.get(position).getThoughts());
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size();

    }





}