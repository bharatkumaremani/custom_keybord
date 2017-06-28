package com.klinker.android.emoji_keyboard.view;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;

/**
 * Created by STELLENT on 5/31/2017.
 */
public class keyboard_video {

    private Context context;
    private BaseAdapter adapter;

    public keyboard_video(Context context, BaseAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    public View getView() {

        final GridView emojiGrid = new GridView(context);

        emojiGrid.setAdapter(adapter);
        return emojiGrid;
    }
}
