package com.example.evento;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageEvents;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.evento.Model.Events;
import com.example.evento.common.Common;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.events.Event;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.auth.FirebaseUser.*;

public class EventDetailActivity extends AppCompatActivity {

    TextView eventName,eventDate,eventTime,eventDescription;
    ImageView eventPoster;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRise;
    Button btnRegister;
    String EventId="";
    public String eveName,currentUserName;
    FirebaseDatabase database;
    DatabaseReference mRef,myRef;

    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        //firebase connection
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Events");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString();
        myRef = database.getReference("Users").child(currentUserId).child("MyEvents");

        //initialize
        eventName = findViewById(R.id.event_name);
        eventDate = findViewById(R.id.event_date);
        eventTime = findViewById(R.id.event_time);
        eventDescription = findViewById(R.id.event_description);
        eventPoster = findViewById(R.id.eventImg);
        btnRegister = findViewById(R.id.registerBtn);
        btnRise = findViewById(R.id.btnImIn);


        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        mRequestQue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        btnRise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventDetailActivity.this,"I'm in",Toast.LENGTH_SHORT).show();
                sendNotification();
            }
        });





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

    private void sendNotification() {

        JSONObject json = new JSONObject();
        try {
            json.put("to","/topics/"+"news");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",currentUserName);
            notificationObj.put("body","is going to attend "+eveName+" Event");
            json.put("notification",notificationObj);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("MUR", "onResponse: ");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: "+error.networkResponse);
                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAAVJ3DCk:APA91bHuha0AYpQue2ufYbaXy2WKgiVghZX4_rEiYDobiRZXu-DBHxvJgex18QYiGps4I2HwuGmWzuIt1W9R3gfB_P5pdC6GzfYZCFoXuzJ3jcQLd0Kp-AW30aXyn6Y6cvRdwF4whKWL");
                    return header;
                }
            };
            mRequestQue.add(request);
        }
        catch (JSONException e)

        {
            e.printStackTrace();
        }
    }


    public void getDetailEvent(String eventId) {

        mRef.child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Events event = dataSnapshot.getValue(Events.class);
                //set image
                Picasso.get().load(event.getPoster())
                        .into(eventPoster);
                final String poster = event.getPoster();

                eveName = event.getName();

                eventName.setText(event.getName());
                eventDate.setText(event.getDate());
                eventTime.setText(event.getTime());
                eventDescription.setText(event.getDescription());
                collapsingToolbarLayout.setTitle(event.getName());
                final Events clickItem = event;
                btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = eventName.getText().toString();
                        String date = eventDate.getText().toString();
                        String time = eventTime.getText().toString();
                        String desc = eventDescription.getText().toString();

                        Events data = new Events(name,poster,date,time,desc);
                        myRef.child(name).setValue(data);

                        Toast.makeText(EventDetailActivity.this,"Successfully registered to "+clickItem.getName(),Toast.LENGTH_SHORT).show();
                        btnRegister.setTextColor(Color.WHITE);
                        btnRegister.setText("Registered");

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}