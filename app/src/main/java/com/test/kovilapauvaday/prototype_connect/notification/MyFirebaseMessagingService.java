package com.test.kovilapauvaday.prototype_connect.notification;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.test.kovilapauvaday.prototype_connect.DonnesAmie;
import com.test.kovilapauvaday.prototype_connect.messages.ChatActivity;
import com.test.kovilapauvaday.prototype_connect.HomeActivity;
import com.test.kovilapauvaday.prototype_connect.users_amies_profile.ProfileActivity;
import com.test.kovilapauvaday.prototype_connect.R;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by ARAM on 18/12/2017.
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private String notification_title;
    private String notification_message;

    Intent resultIntent = null;

    NotificationCompat.Builder mBuilder;

    //@Override
    @SuppressLint("LongLogTag")
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        notification_title = remoteMessage.getNotification().getTitle();
        notification_message = remoteMessage.getNotification().getBody();

        String id_envoyeur = remoteMessage.getData().get("id_envoyeur");
        String latitude = remoteMessage.getData().get("latitude");
        String longtitude = remoteMessage.getData().get("longtitude");
        String type_class = remoteMessage.getData().get("type_class");
        String user_pseudo = remoteMessage.getData().get("user_pseudo");

        if(type_class.equals("ProfileActivity")) {
            resultIntent = new Intent(this, ProfileActivity.class);

            resultIntent.putExtra("id_envoyeur", id_envoyeur);

        } else if(type_class.equals("ChatActivity")) {
            resultIntent = new Intent(this, ChatActivity.class);

            resultIntent.putExtra("id_envoyeur", id_envoyeur);
            resultIntent.putExtra("user_pseudo", user_pseudo);
            resultIntent.putExtra("latitude", latitude);
            resultIntent.putExtra("longtitude", longtitude);

            DonnesAmie.latitude = latitude;
            DonnesAmie.longtitude = longtitude;
            DonnesAmie.pseudo = user_pseudo;

        } else {
            resultIntent = new Intent(this, HomeActivity.class);

            resultIntent.putExtra("id_envoyeur", id_envoyeur);
            resultIntent.putExtra("user_pseudo", user_pseudo);
            resultIntent.putExtra("latitude", latitude);
            resultIntent.putExtra("longtitude", longtitude);
            resultIntent.putExtra("type_class", type_class);

            DonnesAmie.latitude = latitude;
            DonnesAmie.longtitude = longtitude;
            DonnesAmie.pseudo = user_pseudo;
        }

        Uri defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notification_title)
                        .setContentText(notification_message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundURI);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(pendingIntent);

        int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}

