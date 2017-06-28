package com.klinker.android.emoji_keyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.klinker.android.emoji_keyboard.view.EmojiKeyboardView;
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

public class EmojiKeyboardService extends InputMethodService {

    private EmojiKeyboardView emojiKeyboardView;
String result_get;
    private InputConnection inputConnection;
    public static ArrayList<String> video_array;

    private InputMethodManager previousInputMethodManager;
    private IBinder iBinder;

    private static Context staticApplicationContext;

    public static Context getStaticApplicationContext() {
        return staticApplicationContext;
    }

    public EmojiKeyboardService() {
        super();

        if (Build.VERSION.SDK_INT >= 17) {
            enableHardwareAcceleration();
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


    @Override
    public View onCreateInputView() {



        staticApplicationContext = getApplicationContext();

        previousInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        iBinder = this.getWindow().getWindow().getAttributes().token;


        emojiKeyboardView = (EmojiKeyboardView) getLayoutInflater()
                .inflate(R.layout.emoji_keyboard_layout, null);


        return emojiKeyboardView.getView();
    }




    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);

        inputConnection = getCurrentInputConnection();
    }

    public void sendText(String text) {
        inputConnection.commitText(text, 1);
    }

    public void sendDownKeyEvent(int keyEventCode, int flags) {
        inputConnection.sendKeyEvent(
                new KeyEvent(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        KeyEvent.ACTION_DOWN,
                        keyEventCode,
                        0,
                        flags
                )
        );
    }

    public void sendUpKeyEvent(int keyEventCode, int flags) {
        inputConnection.sendKeyEvent(
                new KeyEvent(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        KeyEvent.ACTION_UP,
                        keyEventCode,
                        0,
                        flags
                )
        );
    }
    public void sendDownAndUpKeyEvent(int keyEventCode, int flags){
        sendDownKeyEvent(keyEventCode, flags);
        sendUpKeyEvent(keyEventCode, flags);
    }


    public void switchToPreviousInputMethod() {

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(25);

        try {
            previousInputMethodManager.switchToLastInputMethod(iBinder);
        } catch (Throwable t) { // java.lang.NoSuchMethodError if API_level<11
            Context context = getApplicationContext();
            CharSequence text = "Unfortunately input method switching isn't supported in your version of Android! You will have to do it manually :(";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}
