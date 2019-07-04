package com.example.byebcare;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import static java.lang.Thread.sleep;

public class BackgroundService extends IntentService {

    String channelId = "CHANNEL_NO_1";

    private String htmlPageUrl = "http://10.4.104.131";
    private String htmlContentInStringFormat = "";
    private JSONObject jsonObject;
    private String notiText = "";
    private HashMap<String, String> list = new HashMap<>();

    public BackgroundService() {
        super("BACKGROUNDSERVICE");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String dataString = workIntent.getDataString();
        list.put("O", "체온");
        list.put("X", "X축");
        list.put("Y", "Y축");
        while (true) {
            try {
                emergencyCall();
                Document doc;
                doc = Jsoup.connect(htmlPageUrl).timeout(10000).get();
                if (doc != null) {
                    htmlContentInStringFormat = doc.text();
                    jsonObject = new JSONObject(htmlContentInStringFormat);
                    Iterator<String> it = jsonObject.keys();
                    while (it.hasNext()) {
                        String key = it.next();
                        notiText += (list.get(key) + " : " + jsonObject.get(key) + " ");
                    }
                    sendNotification("Your Baby's Current State", notiText);
                    notiText = "";

                    sleep(60000);
                }
            } catch (IOException e) {
                try {
                    sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                sendNotification("ByeBCare", "Poor Server Connection. Please Check Your Baby.");
                System.out.println(e.getMessage() + "  my");
            } catch (InterruptedException e) {
                System.out.println("INDOT");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendNotification(String titleText, String contentText) {
        String CHANNEL_ID = createNotificationChannel();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(titleText)
                        .setContentText(contentText);

        if(CHANNEL_ID == null) {
            builder.setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_MAX);
        } else {
            builder.setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setChannelId(channelId);
        }


        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://10.4.104.131"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(001, builder.build());
    }

    private String createNotificationChannel() {
        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.

            // The user-visible name of the channel.
            CharSequence channelName = "N";
            // The user-visible description of the channel.
            String channelDescription = "d";
            int channelImportance = NotificationManager.IMPORTANCE_HIGH;
            boolean channelEnableVibrate = true;
            int channelLockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel =
                    new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }


    private void emergencyCall() {
        if (true) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(("tel:911111")));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
