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

public class BackgroundService extends Service {

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private JSONObject jsonObject;
    private HashMap<String, String> list = new HashMap<>();

    private String htmlPageUrl = G.SERVER_URL;
    private String htmlContentInStringFormat = "";
    private String notiText = "";
    private Boolean emergencyCallCanceled = false;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case G.START_POLLING :
                    sendEmptyMessage(G.DO_POLLING);
                    break;
                case G.STOP_POLLING :
                    removeMessages(G.DO_POLLING);
                    break;
                case G.NOTIFICATION_ACTION :
                    emergencyCallCanceled = true;
                    break;
                case G.EMERGENCY_CALL :
                    if(!emergencyCallCanceled)  { emergencyCall(); }
                    break;
                case G.DO_POLLING :
                    try {
                        Document doc = null;
                        doc = Jsoup.connect(htmlPageUrl).timeout(10000).get();
                        if (doc != null) {
                            htmlContentInStringFormat = doc.text();
                            jsonObject = new JSONObject(htmlContentInStringFormat);

                            if(isBabyInDanger()) {
                                sendNotification("EMERGENCY!!!", "YOUR BABY IS IN DANGER !!!", G.NOTIFICATION_EMERGENCY);
                                sendEmptyMessageDelayed(G.EMERGENCY_CALL, G.EMERGENCY_CALL_DELAY);
                                sendMessageAtFrontOfQueue(obtainMessage(G.STOP_POLLING));
                                break;
                            } else {
                                Iterator<String> it = jsonObject.keys();
                                while (it.hasNext()) {
                                    String key = it.next();
                                    notiText += (list.get(key) + " : " + jsonObject.get(key) + " ");
                                }
                                sendNotification("Your Baby's Current State", notiText, G.NOTIFICATION_DEFAULT);
                                notiText = "";
                            }
                        }
                    } catch (IOException e) {
                        sendNotification("ByeBCare", "Poor Server Connection. Please Check Your Baby.", G.NOTIFICATION_DEFAULT);
                        System.out.println(e.getMessage() + "  my");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendEmptyMessageDelayed(G.DO_POLLING, G.POLLING_FREQUENCY);
                    break;
            }
            //emergencyCall();
            //Stop the service using the startId, so we don't stop the service that we use
            //stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
        startForeground(G.FOREGROUND_ID, createNotification("ByeBCare", "ByeBCare Service is running...",G.NOTIFICATION_FOREGROUND));
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
        msg.what = intent.getIntExtra(G.REQUEST_TYPE, msg.what);
        msg.arg1 = startId;
        if (msg.what == G.NOTIFICATION_ACTION) serviceHandler.sendMessageAtFrontOfQueue(msg);
        else {serviceHandler.sendMessage(msg);}
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

    private Notification createNotification(String titleText, String contentText, int NOTIFICATION_TYPE) {
        String CHANNEL_ID = createNotificationChannel();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_child_care_black_24dp)
                        .setContentTitle(titleText)
                        .setContentText(contentText);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);

        if(CHANNEL_ID == null) {
            builder.setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_MAX);
        } else {
            builder.setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setChannelId(G.CHANNEL_ID);
        }

        switch (NOTIFICATION_TYPE) {
            case G.NOTIFICATION_DEFAULT:
                builder.setTimeoutAfter(G.NOTIFICATION_TIMEOUT);
                break;
            case G.NOTIFICATION_EMERGENCY:
                builder.addAction(createEmergencyAction());
                break;
            case G.NOTIFICATION_FOREGROUND:
                break;
        }
        return builder.build();
    }

    private NotificationCompat.Action createEmergencyAction() {
        Intent intent = new Intent();
        intent.setClass(getApplication(), BackgroundService.class);
        intent.putExtra(G.REQUEST_TYPE, G.NOTIFICATION_ACTION);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

       return new NotificationCompat.Action.Builder(R.drawable.ic_child_care_black_24dp, "NO NEED TO CALL EMERGENCY",pendingIntent)
                .build();
    }

    private void sendNotification(String titleText, String contentText, int NOTIFICATION_TYPE) {
        Notification notification = createNotification(titleText, contentText, NOTIFICATION_TYPE);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_TYPE, notification);
    }

    private String createNotificationChannel() {
        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.

            // The user-visible name of the channel.
            CharSequence channelName = G.CHANNEL_NAME;
            // The user-visible description of the channel.
            String channelDescription = G.CHANNEL_DESCRIPTION;
            int channelImportance = NotificationManager.IMPORTANCE_HIGH;
            boolean channelEnableVibrate = true;
            int channelLockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel =
                    new NotificationChannel(G.CHANNEL_ID, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            return notificationChannel.getId();
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }

    private Boolean isBabyInDanger() {
        return true;
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
