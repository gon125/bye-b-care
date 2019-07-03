package com.example.byebcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.content.Intent;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String htmlPageUrl = "http://10.4.104.131";
    private String htmlContentInStringFormat="";

    @BindView(R.id.button) Button button;
    @BindView(R.id.textView) TextView textView;

    @OnClick(R.id.button)
    public void onClick(Button button) {
        button.setText("받음!");
        getData();
        //sendNotification();
    }


    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected  void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected  Void doInBackground(Void... params) {
            try {
                Document doc;
                if ((doc = Jsoup.connect(htmlPageUrl).get()) == null) System.out.println("Server Error....");
                htmlContentInStringFormat = doc.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result) {
            textView.setText(htmlContentInStringFormat);
        }
    }


    private void getData() {
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    private void sendNotification() {
        String CHANNEL_ID = createNotificationChannel();
        String textTitle = "asdf";
        String textContent = "asdfasdfsd";

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                //.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
    private String createNotificationChannel() {
        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = "1";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        button.setText("동기화");

    }
}
