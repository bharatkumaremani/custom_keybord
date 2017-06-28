package com.klinker.android.emoji_keyboard.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.klinker.android.emoji_keyboard.MainSettings;
import com.klinker.android.emoji_keyboard.constants.Apple_EmojiIcons;
import com.klinker.android.emoji_keyboard.constants.EmojiIcons;
import com.klinker.android.emoji_keyboard.constants.EmojiTexts;
import com.klinker.android.emoji_keyboard.constants.Google_EmojiIcons;
import com.klinker.android.emoji_keyboard.view.KeyboardSinglePageView;
import com.klinker.android.emoji_keyboard.view.keyboard_video;

import java.util.ArrayList;

public class EmojiPagerAdapter extends PagerAdapter {

    private final String[] TITLES = { "recent",
                                    "people",
                                    "things",
                                    "nature",
                                    "places",
                                    "symbols",
                                     "video"};

    private ViewPager pager;
    private ArrayList<View> pages;
    private int keyboardHeight;
    ArrayList video;

    public EmojiPagerAdapter(Context context, ViewPager pager, int keyboardHeight) {
        super();

        this.pager = pager;
        this.keyboardHeight = keyboardHeight;
        this.pages = new ArrayList<View>();
        ArrayList test= new ArrayList();
        this.video=video;
        test.add("https://s3.amazonaws.com/tweak-live-videos/5925dacfdd8807000487dfdb.mp4");
        test.add("https://s3.amazonaws.com/tweak-live-videos/5925dacfdd8807000487dfdb.mp4");
        EmojiIcons icons = getPreferedIconSet();
        pages.add(new KeyboardSinglePageView(context, new RecentEmojiAdapter(context)).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.peopleEmojiTexts, icons.getPeopleIconIds())).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.thingsEmojiTexts, icons.getThingsIconIds())).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.natureEmojiTexts, icons.getNatureIconIds())).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.transEmojiTexts, icons.getTransIconIds())).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.otherEmojiTexts, icons.getOtherIconIds())).getView());
        pages.add(new keyboard_video(context, new Video_adapter(context)).getView());
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        pager.addView(pages.get(position), position, keyboardHeight);
        return pages.get(position);
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        pager.removeView(pages.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private EmojiIcons getPreferedIconSet() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pager.getContext());

        if (sharedPreferences
                .getString(MainSettings.CHANGE_ICON_SET_KEY, MainSettings.CHANGE_ICON_SET_VALUE_DEFAULT)
                .equals(MainSettings.CHANGE_ICON_SET_VALUE_GOOGLE)){
            return new Google_EmojiIcons();
        } else if (sharedPreferences
                .getString(MainSettings.CHANGE_ICON_SET_KEY, MainSettings.CHANGE_ICON_SET_VALUE_DEFAULT)
                .equals(MainSettings.CHANGE_ICON_SET_VALUE_APPLE)) {
            return new Apple_EmojiIcons();
        }

        return new Google_EmojiIcons();
    }
}
