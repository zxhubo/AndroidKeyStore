package com.example.cryptutils.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferencesUtils {
    private static final String FILE_NAME="crypto";

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    private SharePreferencesUtils(){}
    private static SharePreferencesUtils sharePreferencesUtils;

    /**
     *
     * @param context
     * @return
     */
    public static SharePreferencesUtils createSharePreferences(Context context)
    {
        sp = context.getApplicationContext().getSharedPreferences(FILE_NAME,context.MODE_PRIVATE);
        editor = sp.edit();
        if (sharePreferencesUtils==null){
            synchronized (SharePreferencesUtils.class){
                if(sharePreferencesUtils==null){
                    sharePreferencesUtils = new SharePreferencesUtils();
                }
            }
        }
        return sharePreferencesUtils;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setString(String key,String value){
        editor.putString(key, value);
        editor.commit();
    }

    /**
     *
     * @param key
     * @return
     */
    public String getString(String key){
        return sp.getString(key,null);
    }
}
