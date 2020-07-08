package com.flixxo.apps.flixxoapp.utils;

import android.os.Build;
import android.util.Base64;
import com.flixxo.apps.flixxoapp.model.Secret;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class CryptoHelper {


    private static byte[] getNextSalt() throws NoSuchAlgorithmException {
        byte[] salt = new byte[10];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SecureRandom.getInstanceStrong().nextBytes(salt);
        } else {
            new SecureRandom().nextBytes(salt);
        }
        return salt;
    }

    private static long getTimestamp() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    private static int getPeriod() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Math.toIntExact(getTimestamp() / 120) | 1;
        } else {
            return safeLongToInt(getTimestamp() / 120) | 1;
        }
    }

    public static String genetareToken(Secret secret, String path) {

        path = path.replace("/api", "");

        try {
            int period = getPeriod();

            String key = String.format("%s:%s", secret.getSecretBase64(), String.valueOf(period));
            byte[] salt = getNextSalt();
            byte[] body = generateHashWithHmac512(path.getBytes(), salt);

            if (body == null) {
                throw new IllegalArgumentException("Something failed in the body");
            }

            byte[] signature = generateHashWithHmac512(body, key.getBytes());

            if (signature == null) {
                throw new IllegalArgumentException("Something failed in the signature");
            }

            // UUID + ":" + BASE64( SALT ) + "$" + BASE64( HS512 ( period + ":" + SECRET , body ) )
            String salt64 = Base64.encodeToString(salt, Base64.NO_WRAP);
            String signature64 = Base64.encodeToString(signature, Base64.NO_WRAP);

            String token = secret.getUuid() + ":" + salt64 + "$" + signature64;

            return Base64.encodeToString(token.getBytes(), Base64.NO_WRAP);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    // HMAC
    private static byte[] generateHashWithHmac512(byte[] message, byte[] key) {
        try {
            final String hashingAlgorithm = "HmacSHA512";

            byte[] bytes = hmac(hashingAlgorithm, key, message);

            return bytes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] hmac(String algorithm, byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(message);
    }

    private static int safeLongToInt(long numLong) {
        if ((int) numLong != numLong) {
            throw new ArithmeticException("Input overflows int.\n");
        }
        return (int) numLong;
    }
}
