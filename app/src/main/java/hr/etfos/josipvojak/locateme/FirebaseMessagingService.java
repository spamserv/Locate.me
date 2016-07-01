package hr.etfos.josipvojak.locateme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by jvojak on 29.6.2016..
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        //Log.d(Constants.KEY_TAG, "From: " + remoteMessage.getFrom());
        //Log.d(Constants.KEY_TAG, "Notification Message Body: " + remoteMessage.getData().get("message"));
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String sender_email = remoteMessage.getData().get("response_email");
        String type = remoteMessage.getData().get("type");

        //Calling method to generate notification
        if(type.equalsIgnoreCase("1")) {
            sendNotification(title, message, sender_email);
        } else if(type.equalsIgnoreCase("2")) {
            String latitude = remoteMessage.getData().get("latitude");
            String longitude = remoteMessage.getData().get("longitude");
            sendLocationNotification(title ,message, sender_email, latitude, longitude);
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String title, String message, String senderEmail) {
        Intent intent = new Intent(this, SuccessActivity.class);
        intent.putExtra(Constants.KEY_RESPONSE_EMAIL, senderEmail);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendLocationNotification(String title, String message, String senderEmail, String latitude, String longitude) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(Constants.KEY_RESPONSE_EMAIL, senderEmail);
        intent.putExtra(Constants.KEY_LATITUDE, latitude);
        intent.putExtra(Constants.KEY_LONGITUDE, longitude);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
