package com.example.cryptutils.util;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoUtils {
    public static String TAG = "MainActivity";
    private static final String STORE_FILE_NAME = "crypto";
    private static final String DEFAULT_SECRETKEY_NAME = "default_secretkey_name";
    private static final String KEYSTORETYPE="AndroidKeyStore";
    private KeyStore keyStore;
    private byte[] iv;
    private static Context context;
    private static CryptoUtils cryptoUtils;
    private CryptoUtils(KeyStore keyStore){
        this.keyStore=keyStore;
    }

    public static CryptoUtils getInstance(Context context){
        KeyStore keyStore;
        File file=new File(context.getFilesDir(),STORE_FILE_NAME);
        if(cryptoUtils==null){
            synchronized (CryptoUtils.class){
                if(cryptoUtils==null){
                    CryptoUtils.context=context;
                    keyStore=createKeyStore(file);
                    initKey(keyStore,file);
                    cryptoUtils=new CryptoUtils(keyStore);
                }
            }
        }

        return cryptoUtils;
    }
    /**
     *
     * @param keyStore
     * @param file
     */
    private static void initKey(KeyStore keyStore, File file){
        if(hasAlias(keyStore,DEFAULT_SECRETKEY_NAME)){
            return;
        }
        KeyGenerator keyGenerator = generateKeyGenerator();
        SecretKey secretKey = keyGenerator.generateKey();
        storeKey(keyStore,file,secretKey);
    }

    /**
     *
     * @param keyStore
     * @param file
     * @param secretKey
     */
    private static void storeKey(KeyStore keyStore, File file, SecretKey secretKey){
        if(Build.VERSION.SDK_INT>=23){
            try {
                keyStore.setKeyEntry(DEFAULT_SECRETKEY_NAME,secretKey,null,null);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

        }else {
            try {
                keyStore.setKeyEntry(DEFAULT_SECRETKEY_NAME,secretKey,null,null);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(file);
                keyStore.store(fout,null);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(fout!=null){
                    close(fout);
                }
            }


        }
    }
    /**
     *
     * @param file
     * @return
     */
    private static KeyStore createKeyStore(File file){
        KeyStore keyStore=null;
        if(Build.VERSION.SDK_INT>=23){
            try {
                keyStore = KeyStore.getInstance(KEYSTORETYPE);
                keyStore.load(null);
                Log.i(TAG,"init KeyStore successful");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(Build.VERSION.SDK_INT>=14){
            try {
                keyStore=KeyStore.getInstance(KeyStore.getDefaultType());
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            FileInputStream fin = null;
            if(!file.exists()){
                try {
                    boolean isSuccess=file.createNewFile();
                    if(!isSuccess){
                        throw new SecurityException("创建内部存储文件失败");
                    }
                    keyStore.load(null,null);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            }else {
                try {
                    fin = new FileInputStream(file);
                    keyStore.load(fin,null);
                    if(fin!=null){
                        fin.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }else {
            throw new RuntimeException("兼容至Android4.0以上");
        }
        return keyStore;
    }

    /**
     *
     * @return
     */
    private static KeyGenerator generateKeyGenerator(){
        KeyGenerator keyGenerator = null;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            try {
                keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            try {
                keyGenerator.init(new KeyGenParameterSpec.Builder(DEFAULT_SECRETKEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(false)
                        // Require that the user has unlocked in the last 30 seconds
        //                            .setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_DURATION_SECONDS)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        } else {
            try {
                keyGenerator = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            SecureRandom secureRandom=null;
    //            if (android.os.Build.VERSION.SDK_INT >= 24) {
    //                secureRandom = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
    //            } else
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                try {
                    secureRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    secureRandom = SecureRandom.getInstance("SHA1PRNG");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            secureRandom.setSeed(generateSeed());
            keyGenerator.init(128, secureRandom);
        }

        return keyGenerator;
    }

    /**
     *
     * @return
     */
    private static byte[] generateSeed() {
        try {
            ByteArrayOutputStream seedBuffer = new ByteArrayOutputStream();
            DataOutputStream seedBufferOut =
                    new DataOutputStream(seedBuffer);
            seedBufferOut.writeLong(System.currentTimeMillis());
            seedBufferOut.writeLong(System.nanoTime());
            seedBufferOut.writeInt(android.os.Process.myPid());
            seedBufferOut.writeInt(android.os.Process.myUid());
            seedBufferOut.write(Build.BOARD.getBytes());
            return seedBuffer.toByteArray();
        } catch (IOException e) {
            throw new SecurityException("Failed to generate seed", e);
        }
    }

    /**
     *
     * @param keyStore
     * @return
     */
    private SecretKey getSecretKey(KeyStore keyStore)
    {
        SecretKey secretKey=null;
        try {
            secretKey= (SecretKey) keyStore.getKey(DEFAULT_SECRETKEY_NAME,null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    /**
     *
     * @param plaintext
     * @return
     */
    public byte[] aesEncrypt(String plaintext){
        SecretKey secretKey = getSecretKey(keyStore);
        byte[] encrypted = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
            iv=cipher.getIV();
            SharePreferencesUtils sp=SharePreferencesUtils.createSharePreferences(context);
            sp.setString("iv",Base64.encodeToString(iv,Base64.DEFAULT));
            encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    /**
     *
     * @param input
     * @return
     */
    public String aesDecrypt(byte[] input){
        String decrypted = null;
        SecretKey secretKey = getSecretKey(keyStore);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SharePreferencesUtils sp=SharePreferencesUtils.createSharePreferences(context);
            byte[] iv=Base64.decode(sp.getString("iv"),Base64.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE,secretKey,new IvParameterSpec(iv));
            byte[] b=cipher.doFinal(input);
            decrypted = new String(b,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted;
    }
    /**
     *
     * @param keyStore
     * @param alias
     * @return
     */
    private static boolean hasAlias(KeyStore keyStore, String alias){
        try {
            if(keyStore!=null&&keyStore.containsAlias(alias))
            {
                return true;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param io
     */
    private static void close(Closeable io){
        if(io!=null){
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param iv
     */
    public void setIv(byte[] iv){
        this.iv=iv;
    }
    /**
     *
     * @return
     */
    public byte[] getIv() {
        return iv;
    }
}
