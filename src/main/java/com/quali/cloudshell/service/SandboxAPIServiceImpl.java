package com.quali.cloudshell.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quali.cloudshell.api.*;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class SandboxAPIServiceImpl implements SandboxAPIService{
    private SandboxAPISpec sandboxAPI = null;
    private String authToken = "";

    public SandboxAPIServiceImpl(final SandboxServiceConnection connection) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (connection.ignoreSsl) {
            ignoreSsl(builder);
        }

        builder.connectTimeout(connection.connectionTimeoutSec, TimeUnit.SECONDS);

        builder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                return autenticationInterceptor(chain);
            }
        });

        OkHttpClient client= builder.build();

        String baseUrl = String.format(connection.protocol + "://%1$s:%2$s",connection.address,connection.port);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();


        sandboxAPI = retrofit.create(SandboxAPISpec.class);

        try {
            authToken= execute(sandboxAPI.login(connection.user)).getData();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void ignoreSsl(OkHttpClient.Builder builder) {
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] cArrr = new X509Certificate[0];
                return cArrr;
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory());

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            builder.hostnameVerifier(hostnameVerifier);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }

    private okhttp3.Response autenticationInterceptor(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        Request newRequest = request.newBuilder()
                .addHeader("Authorization", "Basic " + authToken)
                .addHeader("Content-Type", "application/json")
                .build();

        return chain.proceed(newRequest);
    }

    public ResponseData<String> login(User user) throws RuntimeException, IOException {
        return execute(sandboxAPI.login(user));
    }

    public ResponseData<CreateSandboxResponse[]> getBlueprints() throws RuntimeException, IOException {
        return execute(sandboxAPI.getBlueprint());
    }

    public ResponseData<CreateSandboxResponse> createSandbox(String blueprintId, CreateSandboxRequest sandboxRequest) throws RuntimeException, IOException {
        return execute(sandboxAPI.createSandbox(blueprintId, sandboxRequest));
    }

    public ResponseData<DeleteSandboxResponse> stopSandbox(String blueprintId) throws RuntimeException, IOException {
        return execute(sandboxAPI.stopSandbox(blueprintId));
    }

    public ResponseData<SandboxDetailsResponse> getSandbox(String sandbox) throws RuntimeException, IOException {
        return execute(sandboxAPI.getSandbox(sandbox));
    }

    private static <T> ResponseData<T> parseResponse(final Response<T> response) throws IOException {
        String message = response.message();
        if (!response.isSuccessful()) {
            final String err = response.errorBody().string();
            return ResponseData.error(response.code(),err).setMessage(message);
        }
        return ResponseData.ok(response.body(),response.code()).setMessage(message);
    }

    public <T> ResponseData<T> execute(Call<T> call) throws IOException {
        Response<T> execute = call.execute();
        return parseResponse(execute);
    }
}
