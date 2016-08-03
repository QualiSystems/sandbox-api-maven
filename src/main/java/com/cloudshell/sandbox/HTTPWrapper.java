package com.cloudshell.sandbox;

import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class HTTPWrapper {

    public static RestResponse ExecuteGet(String url, String token, boolean IgnoreSSL) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClient client = CreateClient(IgnoreSSL);
        HttpGet request = new HttpGet(url);
        request.addHeader("Authorization", "Basic " + token);
        HttpResponse response = null;
        int responseCode = 0;
        try {
            response = client.execute(request);
            responseCode = response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line;
        String out = "";
        try {
            while ((line = rd.readLine()) != null) {
                out += line;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RestResponse(out, responseCode);

    }

    private static HttpClient CreateClient(boolean IgnoreSSL) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        if (IgnoreSSL)
        {
            return HttpClients.custom()
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                    .build()
                            )
                    ).build();
        }
        return HttpClientBuilder.create().build();
    }

    public static JSONObject ExecutePost(String url, String token, StringEntity params, boolean IgnoreSSL)
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        HttpClient client = CreateClient(IgnoreSSL);
        HttpPost request = new HttpPost(url);
        request.addHeader("Authorization", "Basic " + token);
        request.setHeader("Content-type", "application/json");
        request.setEntity(params);
        HttpResponse response = null;
        int responseCode = 0;

        try {
            response = client.execute(request);
            responseCode = response.getStatusLine().getStatusCode();

        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line;
        String out = "";
        try {
            while ((line = rd.readLine()) != null) {
                out += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        RestResponse result = new RestResponse(out, responseCode);
        return JSONObject.fromObject(result.getContent());
    }

    public static RestResponse InvokeLogin(String url, String user, String password, String domain, boolean IgnoreSSL) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        url = url + "/Login";
        HttpClient client = CreateClient(IgnoreSSL);
        StringBuilder result = new StringBuilder();
        try {
            HttpPut putRequest = new HttpPut(url);
            putRequest.addHeader("Content-Type", "application/json");
            putRequest.addHeader("Accept", "application/json");
            JSONObject keyArg = new JSONObject();
            keyArg.put("username", user);
            keyArg.put("password", password);
            keyArg.put("domain", domain);
            StringEntity input;
            try {
                input = new StringEntity(keyArg.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw e;
            }
            putRequest.setEntity(input);
            HttpResponse response = client.execute(putRequest);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("Failed to login: "
                        + statusCode);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (response.getEntity().getContent())));
            String output;
            while ((output = br.readLine()) != null) {
                result.append(output);
            }

            String out =  result.toString().replace("\"","");
            return new RestResponse(out,statusCode);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
