package com.veritas.veritas.Util;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.KeyStore;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyStoreHelper {

    private static final String KEY_ALIAS = "ACCESS_TOKEN_KEY";

    public static void generateKey(KeyStore keyStore) throws Exception {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());
            keyGenerator.generateKey();
        }
    }

    public static SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        generateKey(keyStore);
        return ((SecretKey) keyStore.getKey(KEY_ALIAS, null));
    }
}