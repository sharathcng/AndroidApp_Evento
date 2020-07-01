package com.example.evento.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.evento.Model.EventStatusModel;
import com.example.evento.MyWallActivity;
import com.example.evento.R;
import com.example.evento.common.Common;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusNotification extends Service implements ChildEventListener {

    FirebaseDatabase db;
    DatabaseReference mRef;

    public StatusNotification() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance();
        mRef = db.getReference("Events");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mRef.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        EventStatusModel event = dataSnapshot.getValue(EventStatusModel.class);
        showNotification(dataSnapshot.getKey(),event);
    }

    private void showNotification(String key, EventStatusModel event) {

        Intent intent = new Intent(getBaseContext(), MyWallActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel=
                    new NotificationChannel("eventStatus","eventStatus",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),"eventStatus");

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Evento")
                .setContentText(event.getName()+" Event "+ Common.convertCodeToStatus(event.getStatus()))
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
