package com.klinker.android.emoji_keyboard.adapter;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.VideoView;

import com.klinker.android.emoji_keyboard.EmojiKeyboardService;

import java.util.ArrayList;

/**
 * Created by STELLENT on 5/31/2017.
 */
public class VideoEmojiAdapter extends BaseAdapter {

    protected EmojiKeyboardService emojiKeyboardService;
    protected ArrayList<String> emojiTexts_video=EmojiKeyboardService.video_array;

    public VideoEmojiAdapter(EmojiKeyboardService emojiKeyboardService ) {
        this.emojiKeyboardService = emojiKeyboardService;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final VideoView imageView;

        VideoView videoView;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            videoView = new VideoView(emojiKeyboardService);
            videoView.setFocusable(true);
            if(emojiTexts_video!=null)
            videoView.setVideoURI(Uri.parse(emojiTexts_video.get(position)));
            videoView.setLayoutParams(new GridView.LayoutParams(160, 120));
            // videoView.setScaleType(VideoView.ScaleType.CENTER_CROP);
            videoView.setPadding(8, 8, 8, 8);
            videoView.start();
        } else {
            videoView = (VideoView) convertView;
            videoView.pause();
        }
       /* if (convertView == null) {
            imageView = new VideoView(emojiKeyboardService);
            int scale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, emojiKeyboardService.getResources().getDisplayMetrics());
            imageView.setPadding(scale, (int) (scale * 1.2), scale, (int) (scale * 1.2));

        } else {
            imageView = (VideoView) convertView;
        }*/


        /*imageView.setBackgroundResource(R.drawable.btn_background);
        imageView.setVideoURI(Uri.parse(emojiTexts_video.get(position)));
        imageView.pause();*/

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emojiTexts_video!=null)
                emojiKeyboardService.sendText(emojiTexts_video.get(position));
            }
        });

        return videoView;
    }

    @Override
    public int getCount() {
        if(emojiTexts_video!=null)
        return emojiTexts_video.size();
        else return 0;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
