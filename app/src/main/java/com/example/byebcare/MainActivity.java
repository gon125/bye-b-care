package com.example.byebcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private JSONObject jsonObject;
    private Boolean babyIsInDanger = false;
    @BindView(R.id.button) Button button;
    @BindView(R.id.textView) TextView textView;

    @OnClick(R.id.button)
    public void onClick(Button button) {
        button.setText("받음!");
        //getData();
        if(isBabyInDanger()) sendNotification();
    }


    private Boolean isBabyInDanger() {
        evaluateData();

        if (babyIsInDanger) return true;
        else return false;
    }

    private void evaluateData() {
        babyIsInDanger = true;
    }

    private void getData() {
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask(textView, jsonObject);
        jsoupAsyncTask.execute();
    }

    private void sendNotification() {


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, null)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("dasdf")
                .setContentText("asdfasdfasfd")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://10.4.104.131"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(001, builder.build());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        button.setText("동기화");

    }
}
