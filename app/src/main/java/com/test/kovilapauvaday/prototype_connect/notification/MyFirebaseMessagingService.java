package com.test.kovilapauvaday.prototype_connect.notification;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.test.kovilapauvaday.prototype_connect.ChatActivity;
import com.test.kovilapauvaday.prototype_connect.HomeActivity;
import com.test.kovilapauvaday.prototype_connect.MainActivity;
import com.test.kovilapauvaday.prototype_connect.ProfileActivity;
import com.test.kovilapauvaday.prototype_connect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

        //String

        //String click_action = remoteMessage.getNotification().getClickAction();
        String from_user_id = remoteMessage.getData().get("from_user_id");
        String latitude = remoteMessage.getData().get("latitude");
        String longtitude = remoteMessage.getData().get("longtitude");
        String type_class = remoteMessage.getData().get("type_class");
        String user_pseudo = remoteMessage.getData().get("user_pseudo");

        Log.i("######################################################### MyFirebaseMessagingService", "latitude : " + latitude);
        Log.i("######################################################### MyFirebaseMessagingService", "longtitude : " + longtitude);
        Log.i("######################################################### MyFirebaseMessagingService", "user_pseudo : " + user_pseudo);
        Log.i("######################################################### MyFirebaseMessagingService", "from_user_id : " + from_user_id);
        Log.i("######################################################### MyFirebaseMessagingService", "type_class : " + type_class);


        Log.i("notification", "service");
        Log.i("notification", "from_user_id: " + from_user_id);


        Uri defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notification_title)
                        .setContentText(notification_message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundURI);



        if(type_class.equals("ProfileActivity")) {
            resultIntent = new Intent(this, ProfileActivity.class);
            Log.i("######################################################### MyFirebaseMessagingService", "if -> ProfileActivity");
            resultIntent.putExtra("from_user_id", from_user_id);
            //resultIntent.putExtra("click_action", getBaseContext().toString());

        } else if(type_class.equals("ChatActivity")) {
            resultIntent = new Intent(this, ChatActivity.class);

            Log.i("######################################################### MyFirebaseMessagingService", "if -> ChatActivity");

            resultIntent.putExtra("from_user_id", from_user_id);
            resultIntent.putExtra("user_pseudo", user_pseudo);
            resultIntent.putExtra("latitude", latitude);
            resultIntent.putExtra("longtitude", longtitude);


        } else {
            resultIntent = new Intent(this, HomeActivity.class);

            Log.i("######################################################### MyFirebaseMessagingService", "if -> HomeActivity");

            resultIntent.putExtra("from_user_id", from_user_id);
            resultIntent.putExtra("user_pseudo", user_pseudo);
            resultIntent.putExtra("latitude", latitude);
            resultIntent.putExtra("longtitude", longtitude);
            resultIntent.putExtra("type_class", type_class);
        }


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







    /*private void receptionAmitie(){
        Uri defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notification_title)
                        .setContentText(notification_message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundURI);;

        //Intent resultIntent = new Intent(this, ProfileActivity.class);
        //resultIntent.putExtra("user_id", notification_message);
        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("user_id", from_user_id);
        //Toast.makeText(getBaseContext(), click_action, Toast.LENGTH_LONG).show();

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);




        int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }*/










    /*private void receptionCordonnes(){
        Uri defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notification_title)
                        .setContentText(notification_message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundURI);;


        //double lat_lon[] = stringCordones(notification_message);

        //Intent resultIntent = new Intent(click_action);
        Intent resultIntent = new Intent(this, HomeActivity.class);
        //resultIntent.putExtra("ami_latitude", lat_lon[0]);
        //resultIntent.putExtra("ami_longtitude", lat_lon[1]);
        resultIntent.putExtra("ami_pseudo", notification_title);//stringPseudo(notification_title));
        //stringCordones(notification_message);



        //resultIntent.putExtra("user_id", from_user_id);
        //Toast.makeText(getBaseContext(), click_action, Toast.LENGTH_LONG).show();

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);




        int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }



    private double[] stringCordones(String message){
        //Mes cordones :
        double lat_lon[] = new double[2];
        String cords = message.substring(14);
        int i=0;
        String lat = "";
        String lon = "";

        while(i < cords.length() - 1 && (! cords.substring(i, i+1).equals(","))){
            i++;
        }


        lat = cords.substring(0, i);
        lon = cords.substring(i+2, cords.length());

        lat_lon[0] = new Double(lat);
        lat_lon[1] = new Double(lon);

        return lat_lon;
    }

    private String stringPseudo(String titre) {
        int i=0;

        while(i < titre.length() - 1 && (! titre.substring(i, i+1).equals(" "))){
            i++;
        }

        return titre.substring(0, i);
    }*/
}






















/*import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.aram.projet_gps_tpdev.R;
import com.google.firebase.messaging.RemoteMessage;



public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitre = remoteMessage.getNotification().getTitle();
        String notificationCorps = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();

        String from_user_id = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notificationTitre)
                        .setContentText(notificationCorps);


        Intent intent = new Intent(click_action);
        intent.putExtra("user_id", from_user_id);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(pendingIntent);




        int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }
}*/











/*import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.aram.projet_gps_tpdev.MainActivity;
import com.example.aram.projet_gps_tpdev.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage.getNotification().getBody());
    }


    private void sendNotification(String messageBody){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        notificationBuilder.setContentTitle("Im APP");
        notificationBuilder.setContentText(messageBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundURI);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

}*/
