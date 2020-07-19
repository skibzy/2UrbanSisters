package com.twourbansisters.app.includes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.twourbansisters.app.LoginActivity;
import com.twourbansisters.app.MainActivity;
import com.twourbansisters.app.R;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Utils{

    private Context _context;

    public Utils(Context context){
        this._context = context;
    }


    public boolean haveNetwork(){
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)_context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public static SecretKey generateKey(String str)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        return new SecretKeySpec(str.getBytes(), "AES");
    }

    public static byte[] encryptMsg(String message, SecretKey secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
        /* Encrypt the message. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        return cipherText;
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }

    public static String capitalize(String str, String state) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        if (state.equals("first")) {
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        } else if (state.equals("all")) {
            return str.toUpperCase();
        } else {
            return str.toLowerCase();
        }
    }

    public boolean validateEmail(String x) {
        String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (x.matches(pattern)) {
            return true;
        }
        return false;
    }

    public void viewToggle(final View ini, final View sub){
        if (ini.isShown()) {
            sub.setVisibility(View.VISIBLE);
            sub.setAlpha(0);

            sub.animate().setDuration(1500).alpha(1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    sub.setVisibility(View.VISIBLE);
                }
            });

            ini.animate().setDuration(1500).setStartDelay(500).alpha(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ini.setVisibility(View.GONE);
                }
            });
        } else {
            ini.setVisibility(View.VISIBLE);
            ini.setAlpha(0);

            ini.animate().setDuration(1500).alpha(1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ini.setVisibility(View.VISIBLE);
                }
            });

            sub.animate().setDuration(1500).setStartDelay(500).alpha(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    sub.setVisibility(View.GONE);
                }
            });
        }
    }

    public void menuSwitch(int item) {
        switch (item) {
            case R.id.nav_home:
                _context.startActivity(new Intent(_context.getApplicationContext(),
                        MainActivity.class));
            case R.id.nav_logout:
                FirebaseAuth fb = FirebaseAuth.getInstance();
                fb.signOut();
                _context.startActivity(new Intent(_context.getApplicationContext(),
                        LoginActivity.class));
        }
    }
}