package com.gadihkaratau.lamejorescancionesreik.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String MyPREFERENCES = "MyPrefs";
    private static final String SHUFFLE_KEY = "Shuffle";
    private static final String REPEAT_KEY = "repeat";
    private static final String HAS_PERMISSIONS_KEY = "hasPermission";
    private static final String HAS_GUIDE_AUDIO = "hasGuideAudio";
    private static final String HAS_GUIDE_LYRIC = "hasGuideLyric";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public static void setShuffleKey(Context context, boolean isShuffle) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(SHUFFLE_KEY, isShuffle);
        editor.apply();
    }

    public static boolean getShuffleKey(Context context) {
        return getSharedPreferences(context).getBoolean(SHUFFLE_KEY, false);
    }

    public static void setRepeatKey(Context context, int modeRepeat) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(REPEAT_KEY, modeRepeat);
        editor.apply();
    }

    public static int getRepeatKey(Context context) {
        return getSharedPreferences(context).getInt(REPEAT_KEY, 2);
    }

    public static void setHasPermissionsKey(Context context, boolean isShuffle) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(HAS_PERMISSIONS_KEY, isShuffle);
        editor.apply();
    }

    public static boolean getHasPermissionsKey(Context context) {
        return getSharedPreferences(context).getBoolean(HAS_PERMISSIONS_KEY, false);
    }

    public static void setHasGuideAudio(Context context, boolean isOk) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(HAS_GUIDE_AUDIO, isOk);
        editor.apply();
    }

    public static boolean geHasGuideAudio(Context context) {
        return getSharedPreferences(context).getBoolean(HAS_GUIDE_AUDIO, false);
    }

    public static void setIsOkGuideLyric(Context context, boolean isOkGuide) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(HAS_GUIDE_LYRIC, isOkGuide);
        editor.apply();
    }

    public static boolean getIsOkGuideLyric(Context context) {
        return getSharedPreferences(context).getBoolean(HAS_GUIDE_LYRIC, false);
    }


}
