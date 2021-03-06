package com.example.evento;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.evento.Model.Events;
import com.example.evento.common.SessionManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    TextView eventName, eventDate, eventTime, eventDescription, form_url;
    ImageView eventPoster;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRise, btnShare;
    Button btnRegister;
    String EventId = "";
    public String eveName;
    FirebaseDatabase database;
    DatabaseReference mRef, registerRef,myEventsRef;

    String fullName,gender, phoneNo, email;
    SessionManager sessionManager;

    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        //firebase connection
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Ongoing Events");

        registerRef = database.getReference("Registrations");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Event Details");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //initialize
        eventName = findViewById(R.id.event_name);
        eventDate = findViewById(R.id.event_date);
        eventTime = findViewById(R.id.event_time);
        eventDescription = findViewById(R.id.event_description);
        eventPoster = findViewById(R.id.eventImg);
        btnRegister = findViewById(R.id.registerBtn);
        btnRise = findViewById(R.id.btnImIn);
        btnShare = findViewById(R.id.btnShare);
        form_url = findViewById(R.id.form_url);
        form_url.setMovementMethod(LinkMovementMethod.getInstance());


        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        mRequestQue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        btnRise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventDetailActivity.this, "I'm in", Toast.LENGTH_SHORT).show();
                sendNotification();
            }
        });


        //get eventId from intent
        if (getIntent() != null) {
            EventId = getIntent().getStringExtra("EventId");
        }
        if (!EventId.isEmpty()) {
            getDetailEvent(EventId);
        }


        sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();

         fullName = userDetails.get(SessionManager.KEY_FULLNAME);
         gender = userDetails.get(SessionManager.KEY_GENDER);
         phoneNo = userDetails.get(SessionManager.KEY_PHONENUMBER);
         email = userDetails.get(SessionManager.KEY_EMAIL);

        myEventsRef = database.getReference("Users").child(phoneNo).child("MyEvents");

//        thisEventId =myRef.child(EventId);
//        thatEventId=mRef.child(EventId);

//        if (thisEventId.toString()==thatEventId.toString()){
//            btnRegister.setTextColor(Color.WHITE);
//            btnRegister.setText("Registered");
//        }


    }

    private void sendNotification() {

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + "news");
            JSONObject notificationObj = new JSONObject();
//            notificationObj.put("title",currentUserName);
            notificationObj.put("body", fullName+" is going to attend " + eveName + " Event");
            json.put("notification", notificationObj);


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
                    Log.d("MUR", "onError: " + error.networkResponse);
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAAVJ3DCk:APA91bHuha0AYpQue2ufYbaXy2WKgiVghZX4_rEiYDobiRZXu-DBHxvJgex18QYiGps4I2HwuGmWzuIt1W9R3gfB_P5pdC6GzfYZCFoXuzJ3jcQLd0Kp-AW30aXyn6Y6cvRdwF4whKWL");
                    return header;
                }
            };
            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDetailEvent(final String eventId) {


        mRef.child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final Events event = dataSnapshot.getValue(Events.class);
                //set image
                Picasso.get().load(event.getPoster())
                        .into(eventPoster);
                final String poster = event.getPoster();


                eveName = event.getName();
                eventName.setText(event.getName());
                eventDate.setText(event.getDate());
                eventTime.setText(event.getTime());
                form_url.setText(event.getUrl());

                eventDescription.setText(event.getDescription());
                collapsingToolbarLayout.setTitle(event.getName());
                final Events clickItem = event;

                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Event Name: " + event.getName() + "\nEvent Date: "
                                + event.getDate() + "\nEvent Time: " + event.getTime() + "\nEvent form: " + event.getUrl()
                                + "\nEvent Description: " + event.getDescription());
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    }
                });


                btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String name = eventName.getText().toString();
                        String date = eventDate.getText().toString();
                        String time = eventTime.getText().toString();
                        String desc = eventDescription.getText().toString();
                        String url = form_url.getText().toString();
                        String status = event.getStatus();

                        Events data = new Events(name, poster, date, time, desc, url, status);
                                HashMap<String,String> person = new HashMap<>();
                                person.put("fullName",fullName);
                                person.put("gender",gender);
                                person.put("phoneNo",phoneNo);
                                person.put("email",email);
                                myEventsRef.child(eventId.toString()).setValue(data);
                                registerRef.child(eventId.toString()).child(phoneNo).setValue(person);

                        Toast.makeText(EventDetailActivity.this, "Successfully registered to " + clickItem.getName(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}