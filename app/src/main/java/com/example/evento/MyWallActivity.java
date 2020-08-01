package com.example.evento;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evento.Interface.ItemClickListener;
import com.example.evento.Model.EventStatusModel;
import com.example.evento.ViewHolder.EventStatusUpdate;
import com.example.evento.common.Common;
import com.example.evento.common.SessionManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MyWallActivity extends AppCompatActivity {

    private RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference myEventsRef;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<EventStatusModel, EventStatusUpdate> adapter;
    String phoneNo;

    SessionManager sessionManager;

    LayoutAnimationController layoutAnimationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wall);


        database = FirebaseDatabase.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //session
        sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        phoneNo = userDetails.get(SessionManager.KEY_PHONENUMBER);

        myEventsRef = database.getReference("Users").child(phoneNo).child("MyEvents");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Events");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getBaseContext(),R.anim.layout_item_from_left);
        recycler_menu = findViewById(R.id.listEvents);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);




        loadEventStatus();

    }

    private void loadEventStatus() {

        adapter = new FirebaseRecyclerAdapter<EventStatusModel, EventStatusUpdate>(
                EventStatusModel.class,R.layout.event_list_status_layout,EventStatusUpdate.class,myEventsRef
        ) {
            @Override
            protected void populateViewHolder(EventStatusUpdate eventStatusUpdate, EventStatusModel eventStatus, int i) {

                eventStatusUpdate.event_Name.setText(eventStatus.getName());
                eventStatusUpdate.event_Date.setText(eventStatus.getDate());
                eventStatusUpdate.event_Time.setText(eventStatus.getTime());
                eventStatusUpdate.event_Status.setText(Common.convertCodeToStatus(eventStatus.getStatus()));
                Picasso.get().load(eventStatus.getPoster())
                        .into(eventStatusUpdate.event_Poster);
                final EventStatusModel clickItem = eventStatus;
                eventStatusUpdate.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(MyWallActivity.this,""+clickItem.getName(),Toast.LENGTH_SHORT).show();
                        Intent eventDetail = new Intent(MyWallActivity.this,MyEventsDetailActivity.class);
                        eventDetail.putExtra("EventId",adapter.getRef(position).getKey());
                        startActivity(eventDetail);

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);
        recycler_menu.setLayoutAnimation(layoutAnimationController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}