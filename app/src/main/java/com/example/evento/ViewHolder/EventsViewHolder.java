package com.example.evento.ViewHolder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evento.Interface.ItemClickListener;
import com.example.evento.R;

import org.w3c.dom.Text;

public class EventsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView event_Name;
    public ImageView event_Poster;

    private ItemClickListener itemClickListener;

    public EventsViewHolder(@NonNull View itemView) {
        super(itemView);

        event_Name = itemView.findViewById(R.id.eventName);
        event_Poster = itemView.findViewById(R.id.eventPoster);

        itemView.setOnClickListener(this);

    }

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
