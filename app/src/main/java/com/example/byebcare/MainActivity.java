package com.example.byebcare;

import androidx.appcompat.app.AppCompatActivity;
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
        getData();
        if(isBabyInDanger()) sendNotification();
    }


    private Boolean isBabyInDanger() {
        evaluateData();

        if (babyIsInDanger) return true;
        else return false;
    }

    private void evaluateData() {

    }

    private void getData() {
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask(textView, jsonObject);
        jsoupAsyncTask.execute();
    }

    private void sendNotification() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        button.setText("동기화");

    }
}
