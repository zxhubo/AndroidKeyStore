package com.example.crypt;

import android.content.SharedPreferences;

public class SharePreferencesUtil {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public  SharePreferencesUtil(SharedPreferences sp)
    {
        this.sp = sp;
        initEditor();
    }
    private void initEditor(){
        editor = sp.edit();
    }

    public void setString(String key ,String value){
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key){
        return sp.getString(key,"Null");
    }
}
