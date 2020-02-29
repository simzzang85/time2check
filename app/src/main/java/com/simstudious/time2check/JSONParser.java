package com.simstudious.time2check;

import android.content.Context;
import android.net.ParseException;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.android.volley.VolleyLog.TAG;

public class JSONParser {

    // constructor
    public JSONParser() {
    }
    public String rtnStr = "";
    public String reqUrl="";
    //public Context context;
    public String UrlHeaderResponse(String rtnStr){
        //reqUrl = url;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(rtnStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(4000);
            try{
                //InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                rtnStr =  (urlConnection==null) ? "MalformedURL" : urlConnection.getHeaderField("date");
            }catch (Exception e){
                Log.d("error::::::::", e.toString());
            }
            urlConnection.disconnect();
        }
        catch (IOException e){
            Log.d("in::::::::", e.toString());
            rtnStr = e.toString();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return rtnStr;
    }

    /**
     * Old function NOT USING
     * @param strUrl
     * @return
     */
    public String UrlResponse(String strUrl){
        //reqUrl = url;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            try{
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                rtnStr = readStream(in);
            }catch (IOException e){
            }
            urlConnection.disconnect();
        }
        catch (IOException e){
            rtnStr = e.toString();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return rtnStr;
    }
    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

}
