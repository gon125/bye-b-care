package com.example.byebcare;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Text;

import java.io.IOException;


public class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

    private String htmlPageUrl = "http://10.4.104.131";
    private String htmlContentInStringFormat="";
    private TextView textView;
    private JSONObject jsonObject;

    public JsoupAsyncTask(TextView textView, JSONObject jsonObject) {
     this.textView = textView;
     this.jsonObject = jsonObject;
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
            jsonObject = new JSONObject(htmlContentInStringFormat);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected  void onPostExecute(Void result) {
        textView.setText(htmlContentInStringFormat);
        try {
            System.out.println(jsonObject.get("X"));

            System.out.println(jsonObject.get("Y"));

            System.out.println(jsonObject.get("Z"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}