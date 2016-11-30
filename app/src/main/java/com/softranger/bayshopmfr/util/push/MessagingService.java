package com.softranger.bayshopmfr.util.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.softranger.bayshopmfr.R;
import com.softranger.bayshopmfr.model.NotificationMessage;
import com.softranger.bayshopmfr.ui.awaitingarrival.AwaitingArrivalActivity;
import com.softranger.bayshopmfr.ui.general.MainActivity;
import com.softranger.bayshopmfr.ui.instock.InStockActivity;
import com.softranger.bayshopmfr.ui.pus.PUSParcelActivity;

import java.util.Map;

import io.intercom.android.sdk.push.IntercomPushClient;

/**
 * Created by Eduard Albu on 10/19/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class MessagingService extends FirebaseMessagingService {

    public static final String TAG = "MessagingService";
    private final IntercomPushClient intercomPushClient = new IntercomPushClient();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map message = remoteMessage.getData();

        Log.e(TAG, "Message received");

        if (intercomPushClient.isIntercomPush(message)) {
            intercomPushClient.handlePush(getApplication(), message);
        } else if (remoteMessage.getData().size() > 0) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                NotificationMessage notificationMessage = mapper.readValue(remoteMessage.getData().get("data"), NotificationMessage.class);
                sendNotification(notificationMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param message FCM message body received.
     */
    private void sendNotification(NotificationMessage message) {
        Intent intent;
        switch (message.getAction()) {
            case mf:
                intent = new Intent(this, InStockActivity.class);
                intent.putExtra("id", message.getId());
                break;
            case waitingMf:
                intent = new Intent(this, AwaitingArrivalActivity.class);
                intent.putExtra("id", message.getId());
                break;
            case parcel:
                intent = new Intent(this, PUSParcelActivity.class);
                intent.putExtra("id", message.getId());
                break;
            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }

        int intId = Integer.parseInt(message.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, intId /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(message.getAction().icon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo_48dp))
                .setContentTitle(message.getTitle())
                .setContentText(message.getMessage())
                .setAutoCancel(true)
                .setVibrate(new long[]{3000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message.getMessage()))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(intId /* ID of notification */, notificationBuilder.build());
    }
}
