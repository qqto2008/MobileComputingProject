package com.capa.capa.mobilecomputingproject;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class WeatherData extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... addresses) {
        try {
            URL url = new URL(addresses[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            int data = inputStreamReader.read();
            String content = "";
            char ch;
            while (data !=-1){
                ch = (char) data;
                content = content+ch;
                data = inputStreamReader.read();
            }
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

