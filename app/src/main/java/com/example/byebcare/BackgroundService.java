package com.example.byebcare;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class BackgroundService extends IntentService {

    String channelId = "CHANNEL_NO_1";

    private String htmlPageUrl = "http://10.4.104.131";
    private String htmlContentInStringFormat="";
    private JSONObject jsonObject;
    Boolean serverError = false;
    int i = 0;


    public BackgroundService() {
        super("BACKGROUNDSERVICE");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String dataString = workIntent.getDataString();
        while(true) {
            try {
                Document doc;
                doc = Jsoup.connect(htmlPageUrl).timeout(10000).get();
                htmlContentInStringFormat = doc.text();
                //jsonObject = new JSONObject(htmlContentInStringFormat);
                System.out.println("call" + i); i++;
                if (i % 10 == 0) {
                   sendNotification("아이정보", htmlContentInStringFormat);
                }
                sleep(1000);
            } catch (IOException e) {
                System.out.println(e.getMessage() + "  my");
                serverError = true;

            } catch (InterruptedException e) {
                System.out.println("INDOT");
            } /*catch (JSONException e) {
                e.printStackTrace();
            }*/ finally {
                if (serverError) {
                    sendNotification("ByeBCare", "서버연결상태가 좋지 않습니다. 아이를 확인해주세요.");
                    serverError = false;
                }
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

}
