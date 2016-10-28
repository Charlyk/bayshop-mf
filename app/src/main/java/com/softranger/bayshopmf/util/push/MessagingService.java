package com.softranger.bayshopmf.util.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.softranger.bayshopmf.R;
import com.softranger.bayshopmf.model.NotificationMessage;
import com.softranger.bayshopmf.model.pus.PUSParcelActivity;
import com.softranger.bayshopmf.ui.awaitingarrival.AwaitingArrivalActivity;
import com.softranger.bayshopmf.ui.general.MainActivity;
import com.softranger.bayshopmf.ui.instock.InStockActivity;

/**
 * Created by Eduard Albu on 10/19/16, 10, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class MessagingService extends FirebaseMessagingService {

    public static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                NotificationMessage message = mapper.readValue(remoteMessage.getData().get("data"), NotificationMessage.class);
                sendNotification(message);
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
            case awaiting_declaration:
            case packing:
            case held_by_prohibition:
            case held_by_damage:
            case held_by_user:
            case customs_held:
            case taken_to_delivery:
            case local_depo:
            case packed:
            case received:
            case sent:
            case live:
            case processing:
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
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo_48dp))
                .setContentTitle(message.getTitle())
                .setContentText(message.getMessage())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message.getMessage()))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(intId /* ID of notification */, notificationBuilder.build());
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_local_deposit_44dp : R.mipmap.ic_logo_48dp;
    }
}
