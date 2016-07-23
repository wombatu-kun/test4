package ru.edocs_lab.test4;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class NetworkUtils {
    private static final String TAG = "NETWORK_UTILS";
    private static final String FROM_URL = "https://demo.bankplus.ru/mobws/json/pointsList?version=1.1";
    private static final String REQ_METHOD = "POST";
    private static final String REQ_PROP_COUNT_NAME = "count";
    private static final int READ_TIMEOUT = 10000; /* milliseconds */
    private static final int CONN_TIMEOUT = 15000; /* milliseconds */

    public static boolean checkConnection(Activity ctx) {
        ConnectivityManager connMgr = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getResponseString(String count, Context ctx) {
        String response = "";
        InputStream is = connectToServer(count, ctx);
        if (is != null) {
            try {
                response = readJSONResponse(is);
            } catch (IOException e){
                Log.e(TAG, "LOAD_JSON: " + e);
            }
        }
        return response;
    }

    private static InputStream connectToServer(String count, Context ctx) {
        InputStream is;
        try {
            URL url = new URL(FROM_URL);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setSSLSocketFactory(getSSLSocketFactory(ctx));
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONN_TIMEOUT);
            conn.setRequestMethod(REQ_METHOD);
            conn.setFixedLengthStreamingMode((REQ_PROP_COUNT_NAME+"="+count).getBytes().length);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(REQ_PROP_COUNT_NAME+"="+count);
            out.close();
            is = conn.getInputStream();
            return is;
        } catch (Exception e) {
            Log.e(TAG, "connectToServer error - " + e);
            return null;
        }
    }

    private static SSLSocketFactory getSSLSocketFactory(Context ctx) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = new BufferedInputStream(ctx.getAssets().open("cert.cer"));
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);
        return context.getSocketFactory();
    }

    private static String readJSONResponse(InputStream is) throws IOException {
        BufferedReader reader = null;
        StringBuilder jsonString;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        } finally {
            if (reader != null) reader.close();
        }
        return jsonString.toString();
    }
}
