package com.example.evento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.evento.Interface.ItemClickListener;
import com.example.evento.Model.Events;
import com.example.evento.ViewHolder.EventsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class EventsListActivity extends AppCompatActivity {

    private RecyclerView recycler_menu;

    FirebaseDatabase database;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Events, EventsViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Events");



        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        loadEvents();

    }

    private void loadEvents() {

         adapter = new FirebaseRecyclerAdapter<Events, EventsViewHolder>(
                Events.class,R.layout.event_list,EventsViewHolder.class,mRef
        ) {
            @Override
            protected void populateViewHolder(EventsViewHolder eventsViewHolder, Events events, int i) {

                eventsViewHolder.event_Name.setText(events.getName());
                Picasso.get().load(events.getPoster())
                        .into(eventsViewHolder.event_Poster);
                final Events clickItem = events;
                eventsViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(EventsListActivity.this,""+clickItem.getName(),Toast.LENGTH_SHORT).show();
                        Intent eventDetail = new Intent(EventsListActivity.this,EventDetailActivity.class);
                        eventDetail.putExtra("EventId",adapter.getRef(position).getKey());
                        startActivity(eventDetail);

                    }
                });


            }
        };

        recycler_menu.setAdapter(adapter);

    }
}