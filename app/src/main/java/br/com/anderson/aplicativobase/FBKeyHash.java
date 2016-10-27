package br.com.anderson.aplicativobase;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Devmaker on 21/05/2014.
 */
public class FBKeyHash {

    public static void getHash(Context context) {

        // Add code to print out the key hash
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                //Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                //Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                //Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                //Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
