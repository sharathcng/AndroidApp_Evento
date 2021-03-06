package com.example.evento;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evento.Model.Events;
import com.example.evento.common.SessionManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MyEventsDetailActivity extends AppCompatActivity {

    TextView eventName,eventDate,eventTime,eventDescription;
    ImageView eventPoster;
    Button btnCancel;
    String EventId="",phoneNo;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FirebaseDatabase database;
    DatabaseReference myEventsRef;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events_detail);

        //firebase connection
        database = FirebaseDatabase.getInstance();
        //session
        sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        phoneNo = userDetails.get(SessionManager.KEY_PHONENUMBER);

        myEventsRef = database.getReference("Users").child(phoneNo).child("MyEvents");

        //initialize
        eventName = findViewById(R.id.event_name);
        eventDate = findViewById(R.id.event_date);
        eventTime = findViewById(R.id.event_time);
        eventDescription = findViewById(R.id.event_description);
        eventPoster = findViewById(R.id.eventImg);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Event Details");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        btnCancel = findViewById(R.id.cancelBtn);

        //get eventId from intent
        if (getIntent() != null)
        {
            EventId = getIntent().getStringExtra("EventId");
        }
        if (!EventId.isEmpty())
        {
            getDetailEvent(EventId);
        }

    }

    public void getDetailEvent(final String eventId) {

        myEventsRef.child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Events event = dataSnapshot.getValue(Events.class);
                //set image
                Picasso.get().load(event.getPoster())
                        .into(eventPoster);
//                final String poster = event.getPoster();
                eventName.setText(event.getName());
                eventDate.setText(event.getDate());
                eventTime.setText(event.getTime());
                eventDescription.setText(event.getDescription());
                collapsingToolbarLayout.setTitle(event.getName());

                final Events clickItem = event;

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = eventName.getText().toString();
                        myEventsRef.child(EventId).removeValue();
                        Toast.makeText(MyEventsDetailActivity.this,"Registration cancelled to "+clickItem.getName()+"\nPlease participate and show your talent to all",Toast.LENGTH_SHORT).show();
                        btnCancel.setTextColor(Color.WHITE);
                        btnCancel.setText("Cancelled");
                        Intent eventDetail = new Intent(MyEventsDetailActivity.this,HomeActivity.class);
                        startActivity(eventDetail);

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}