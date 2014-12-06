/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 6.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by dustin on 06/12/14.
 */
public final class SelfSignedCertHelper {
    private static final String TAG = SelfSignedCertHelper.class.getSimpleName();

    /**
     * Create a SSL socket factory for the given keystore with the given password.
     *
     * @param keystoreStream     will be closed automatically
     * @param trustStorePassword
     * @return the socket factory or null
     */
    @Nullable
    public static SSLSocketFactory getPinnedCertSslSocketFactory(@NonNull InputStream keystoreStream, @NonNull String trustStorePassword) {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            trusted.load(keystoreStream, trustStorePassword.toCharArray());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trusted);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (Exception ex) {
            Log.d(TAG, "Problem getting pinned cert ssl socket factory", ex);
        } finally {
            try {
                keystoreStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        return null;
    }

    private SelfSignedCertHelper() {
    }
}
