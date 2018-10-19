package com.example.cryptutils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cryptutils.util.CryptoUtils;
import com.example.cryptutils.util.SharePreferencesUtils;

public class MainActivity extends Activity {
    public static String TAG = "MainActivity";

    private TextView textShow;
    private Button encrypt;
    private Button decrypt;
    private EditText dataText;
    private SharePreferencesUtils sp;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataText = (EditText) findViewById(R.id.data);
        encrypt = (Button) findViewById(R.id.encrypt);
        decrypt = (Button) findViewById(R.id.decrypt);
        textShow = (TextView) findViewById(R.id.text);
        final CryptoUtils cryptoUtils = CryptoUtils.getInstance(this);
        sp = SharePreferencesUtils.createSharePreferences(this);
        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plaintext = dataText.getText().toString();
                String encrypted =Base64.encodeToString(cryptoUtils.aesEncrypt(plaintext),Base64.DEFAULT);
                Log.i(TAG,"encrypted = "+encrypted);
                Log.i(TAG,"iv = "+Base64.encodeToString(cryptoUtils.getIv(),Base64.DEFAULT));
                sp.setString("encrypted",encrypted);
                textShow.setText(encrypted);
            }
        });
        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String encrypted = sp.getString("encrypted");
                String decrypted = cryptoUtils.aesDecrypt(Base64.decode(encrypted,Base64.DEFAULT));
                Log.i(TAG,"decrypted = "+decrypted);
                textShow.setText(decrypted);
            }
        });
    }
}
