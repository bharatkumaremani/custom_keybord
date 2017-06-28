package com.klinker.android.emoji_keyboard.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;

import com.klinker.android.emoji_keyboard.EmojiKeyboardService;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by STELLENT on 5/30/2017.
 */
public class Video_adapter extends VideoEmojiAdapter {


    ArrayList<String> videos;
    Context context;
    LayoutInflater inflter;
    String result_get;

    public Video_adapter(Context context/*, ArrayList<String> videos*/) {
        super((EmojiKeyboardService) context);

        try {
            new video_key().execute();
        }
        catch (Exception e){
            e.printStackTrace();
        }




    }

    class video_key extends AsyncTask<URL, Integer, Long> implements SharedPreferences.OnSharedPreferenceChangeListener {


        public void onPreExecute() {

        }
        @Override
        protected Long doInBackground(URL... params) {
            try {
                HttpPost request = new HttpPost("http://tweakvideos.herokuapp.com");
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/graphql");
                request.setHeader("user-agent", "Yoda");
                try {

                    String  query = "{" + "getTweaksForKeyboard" + "(" +
                            "start:" + 0 + "," +
                            "limit:" + 20 +
                            ")" +
                            "{_id,starttime,endtime,tweakurl,tweakimage}}";;
                    StringEntity entity = new StringEntity(query);
                    entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json; charset=utf-8"));
                    request.setEntity(entity);

                    // Send request to WCF service
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpContext localContext = new BasicHttpContext();
                    HttpResponse response = httpClient.execute(request);
                    int resCode = response.getStatusLine().getStatusCode();
                    InputStream instream = response.getEntity().getContent();
                    String tempresult = convertStreamToString(instream);
                    instream.close();
                    result_get = tempresult.toString();



                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();

                }

            } catch (Exception e) {
                e.printStackTrace();


            }
            return null;
        }
        protected void onPostExecute(Long result1) {
            try {
                Log.e("tag", "onPostExecute: " + result_get);

                JSONObject outerJson = new JSONObject(result_get);
                JSONObject innerJson = outerJson.getJSONObject("data");
                JSONArray jsonarray = innerJson.getJSONArray("getTweaksForKeyboard");

                videos = new ArrayList<String>();

                for (int i = 0; i < jsonarray.length(); i++) {

                    JSONObject jobj = jsonarray.getJSONObject(i);
                    String video = jobj.getString("tweakurl");

                    videos.add(video);


                    Log.e("tweak", "onPostExecute: " + video);

                }

                Video_adapter.this.emojiTexts_video = videos;

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }
    private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }



}
