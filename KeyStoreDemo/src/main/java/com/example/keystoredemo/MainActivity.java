package com.example.keystoredemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.crypt.CryptUtil;
import com.example.crypt.SharePreferencesUtil;

public class MainActivity extends Activity {
    public static String TAG = "MainActivity";

    private TextView textShow;
    private Button encrypt;
    private Button decrypt;
    private EditText dataText;
    private SharePreferencesUtil sp;
    private CryptUtil cryptUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataText = (EditText) findViewById(R.id.data);
        encrypt = (Button) findViewById(R.id.encrypt);
        decrypt = (Button) findViewById(R.id.decrypt);
        textShow = (TextView) findViewById(R.id.text);
        sp = new SharePreferencesUtil(getSharedPreferences("bobby",Context.MODE_PRIVATE));
        cryptUtil = CryptUtil.getInstance();
        cryptUtil.createCrypt(TAG);
        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plaintext = dataText.getText().toString();
                byte[] encrypted = cryptUtil.encrypt(plaintext);
                byte[] iv = cryptUtil.getIv();
                Log.i(TAG,"iv = "+Base64.encodeToString(iv,Base64.DEFAULT));
                Log.i(TAG,"encrypted = "+Base64.encodeToString(encrypted,Base64.DEFAULT));
                sp.setString("iv",Base64.encodeToString(iv,Base64.DEFAULT));
                sp.setString("encrypted",Base64.encodeToString(encrypted,Base64.DEFAULT));
                textShow.setText(Base64.encodeToString(encrypted,Base64.DEFAULT));

            }
        });
        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] iv = Base64.decode(sp.getString("iv"),Base64.DEFAULT);
                String decrypted = cryptUtil.decrypt(Base64.decode(sp.getString("encrypted"),Base64.DEFAULT),iv);
                textShow.setText(decrypted);
            }
        });
    }
}
