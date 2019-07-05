package com.example.byebcare;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Handler.*;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import static java.lang.Thread.sleep;

public class BackgroundService extends Service {

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    String channelId = "CHANNEL_NO_1";

    private String htmlPageUrl = "http://10.4.104.131";
    private String htmlContentInStringFormat = "";
    private JSONObject jsonObject;
    private String notiText = "";
    private HashMap<String, String> list = new HashMap<>();

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override public void handleMessage(Message msg) {
            // would do work here

            //emergencyCall();

            while (true) {
                try {
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
            //Stop the service using the startId, so we don't stop the service
            //stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
        startForeground(1, sendNotification("ByeBCare", "ByeBCare Service is running..."));
        list.put("O", "체온");
        list.put("X", "X축");
        list.put("Y", "Y축");
        list.put("Z", "Z축");
        list.put("A", "주변온도");
        list.put("B", "맥박");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);
        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    private Notification sendNotification(String titleText, String contentText) {
        String CHANNEL_ID = createNotificationChannel();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_child_care_black_24dp)
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


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(001, builder.build());
        return builder.build();
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
