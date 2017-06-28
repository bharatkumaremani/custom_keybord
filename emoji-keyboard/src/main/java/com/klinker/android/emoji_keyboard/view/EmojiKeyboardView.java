package com.klinker.android.emoji_keyboard.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.klinker.android.emoji_keyboard.EmojiKeyboardService;
import com.klinker.android.emoji_keyboard.adapter.EmojiPagerAdapter;
import com.klinker.android.emoji_keyboard.adapter.Video_adapter;
import com.klinker.android.emoji_keyboard_trial.R;

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
import java.util.Arrays;

public class EmojiKeyboardView extends View implements SharedPreferences.OnSharedPreferenceChangeListener{

    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private LinearLayout layout;
    String   result_get;
   public  ArrayList video_array;
    private EmojiPagerAdapter emojiPagerAdapter;
    private EmojiKeyboardService emojiKeyboardService;

    public EmojiKeyboardView(Context context) {
        super(context);
        initialize(context);
    }

    public EmojiKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public EmojiKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {


/*

        try {
            new video_key(context).execute();
        }
        catch (Exception e){
            e.printStackTrace();
        }
*/


        emojiKeyboardService = (EmojiKeyboardService) context;
        LayoutInflater inflater = (LayoutInflater)   getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layout = (LinearLayout) inflater.inflate(R.layout.keyboard_main, null);


        viewPager = (ViewPager) layout.findViewById(R.id.emojiKeyboard);

        pagerSlidingTabStrip = (PagerSlidingTabStrip) layout.findViewById(R.id.emojiCategorytab);

        pagerSlidingTabStrip.setIndicatorColor(getResources().getColor(R.color.pager_color));

        emojiPagerAdapter = new EmojiPagerAdapter(context, viewPager, height);



        viewPager.setAdapter(emojiPagerAdapter);

        setupDeleteButton();

        pagerSlidingTabStrip.setViewPager(viewPager);

        viewPager.setCurrentItem(1);

        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);


    }


    //Service call for getting card number
    class video_key extends AsyncTask<URL, Integer, Long> implements SharedPreferences.OnSharedPreferenceChangeListener {
        Context context;
        public video_key(Context context) {
            this.context=context;
        }

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

                video_array = new ArrayList<String>();

                for (int i = 0; i < jsonarray.length(); i++) {

                    JSONObject jobj = jsonarray.getJSONObject(i);
                    String video = jobj.getString("tweakurl");

                    video_array.add(video);


                    Log.e("tweak", "onPostExecute: " + video);

                }



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

    public View getView() {
        return layout;
    }

    public void notifyDataSetChanged() {
        emojiPagerAdapter.notifyDataSetChanged();
        viewPager.refreshDrawableState();
    }

    private void setupDeleteButton() {

        Button delete = (Button) layout.findViewById(R.id.deleteButton);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiKeyboardService.sendDownAndUpKeyEvent(KeyEvent.KEYCODE_DEL, 0);
            }
        });

        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                emojiKeyboardService.switchToPreviousInputMethod();
                return false;
            }
        });
    }


    private int width;
    private int height;
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = View.MeasureSpec.getSize(heightMeasureSpec);

        Log.d("emojiKeyboardView", width +" : " + height);
        setMeasuredDimension(width, height);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {



        Log.d("sharedPreferenceChange", "function called on change of shared preferences with key " + key);
        if (key.equals("icon_set")){
            emojiPagerAdapter = new EmojiPagerAdapter(getContext(), viewPager, height);
            viewPager.setAdapter(emojiPagerAdapter);
            this.invalidate();
        }
    }
}
