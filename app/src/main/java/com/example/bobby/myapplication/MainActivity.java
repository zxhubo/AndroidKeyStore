package com.example.bobby.myapplication;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends Activity {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    public static String TAG = "MainActivity";
    private TextView textShow;
    private Button encrypt;
    private Button decrypt;
    private EditText dataText;
    private Encryptor encryptor;
    private Decryptor decryptor;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataText = (EditText) findViewById(R.id.data);
        encrypt = (Button) findViewById(R.id.encrypt);
        decrypt = (Button) findViewById(R.id.decrypt);
        textShow = (TextView) findViewById(R.id.text);
        sp = getSharedPreferences("bobby",Context.MODE_PRIVATE);
        editor = sp.edit();

        encryptor = new Encryptor();

        try {
            decryptor = new Decryptor();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
                IOException e) {
            e.printStackTrace();
        }

        encrypt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                encryptText();
            }
        });

        decrypt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                decryptText();
            }
        });
    }




    private void decryptText() {
        try {
//            textShow.setText(decryptor
//                    .decryptData(TAG, encryptor.getEncryption(), encryptor.getIv()));
            SecretKey key = decryptor.getSecretKey(TAG);
//            Log.i(MainActivity.TAG,"deckey_1 = "+Base64.encodeToString(key.getEncoded(),Base64.DEFAULT));
            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] iv = cipher.getIV();
            Log.i(MainActivity.TAG,"iv_1 = "+Base64.encodeToString(iv,Base64.DEFAULT));
            String str = sp.getString("encryptText","Null");
            byte[] b = Base64.decode(str,Base64.DEFAULT);

            textShow.setText(decryptor.decryptData(TAG,b,Base64.decode(sp.getString("iv","Null"),Base64.DEFAULT)));


        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchPaddingException | NoSuchProviderException |
                IOException | InvalidKeyException e) {
            Log.e(TAG, "decryptData() called with: " + e.getMessage(), e);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void encryptText() {

        try {
            final byte[] encryptedText = encryptor
                    .encryptText(TAG, dataText.getText().toString());
            Log.i(TAG,"encryptText="+Base64.encodeToString(encryptedText, Base64.DEFAULT));
            editor.putString("encryptText",Base64.encodeToString(encryptedText,Base64.DEFAULT));
            editor.putString("iv",Base64.encodeToString(encryptor.getIv(),Base64.DEFAULT));
            editor.commit();
            textShow.setText(Base64.encodeToString(encryptedText, Base64.DEFAULT));
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
            Log.e(TAG, "onClick() called with: " + e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }
}
