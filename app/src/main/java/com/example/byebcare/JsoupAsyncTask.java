package com.example.byebcare;

import android.os.AsyncTask;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Text;

import java.io.IOException;


public class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

    private String htmlPageUrl = "http://10.4.104.131";
    private String htmlContentInStringFormat="";
    private TextView textView;

    public JsoupAsyncTask(TextView textView) {
     this.textView = textView;
    }

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