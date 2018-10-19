package com.example.bobby.myapplication;

/**
 * Created by erfli on 2/24/17.
 */

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

class Decryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private KeyStore keyStore;

    Decryptor() throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException {
        initKeyStore();
    }

    private void initKeyStore() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String decryptData(final String alias, final byte[] encryptedData, final byte[] encryptionIv)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        Log.i(MainActivity.TAG,"encryptData length="+String.valueOf(encryptedData.length));
        Log.i(MainActivity.TAG,"Iv length="+String.valueOf(encryptionIv.length));
        Log.i(MainActivity.TAG,"Iv ="+Base64.encodeToString(encryptionIv,Base64.DEFAULT));
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        SecretKey key = getSecretKey(alias);
//        Log.i(MainActivity.TAG,"deckey = "+Base64.encodeToString(key.getEncoded(),Base64.DEFAULT));
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }

    public SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }
}
