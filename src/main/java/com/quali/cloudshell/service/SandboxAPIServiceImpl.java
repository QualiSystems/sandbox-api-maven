package com.quali.cloudshell.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quali.cloudshell.Constants;
import com.quali.cloudshell.api.*;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
import okhttp3.*;
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
    private SandboxAPIAuthInterceptor authInterceptor;
    private SandboxAPISpec sandboxAPI = null;
    private User user = null;

    public SandboxAPIServiceImpl(final SandboxServiceConnection connection) throws SandboxApiException {

        user = connection.user;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (connection.ignoreSsl) {
            ignoreSsl(builder);
        }

        builder.connectTimeout(Constants.TIMEOUT, TimeUnit.SECONDS);

        authInterceptor = new SandboxAPIAuthInterceptor(new SandboxAPISpecProvider() {
            @Override
            public SandboxAPISpec getApi() {
                return sandboxAPI;
            }

            @Override
            public User getUser() {
                return connection.user;
            }
        });

        builder.addInterceptor(authInterceptor);

        builder.authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, okhttp3.Response response) throws IOException {
                return null;
            }
        });

        OkHttpClient client= builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(connection.address)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();


        sandboxAPI = retrofit.create(SandboxAPISpec.class);

        if (connection.address == null)
            throw new SandboxApiException("Failed to obtain CloudShell Sandbox API Server address, Please validate Sandbox API configuration. ");

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

    public ResponseData<String> login() throws RuntimeException, IOException {
        return execute(sandboxAPI.login(user));
    }

    public ResponseData<CreateSandboxResponse[]> getBlueprints() throws RuntimeException, IOException {
        return execute(sandboxAPI.getBlueprint());
    }

    public ResponseData<CreateSandboxResponse> createSandbox(String blueprintId, CreateSandboxRequest sandboxRequest) throws RuntimeException, IOException, SandboxApiException {

        ResponseData<CreateSandboxResponse> responseData = execute(sandboxAPI.createSandbox(blueprintId, sandboxRequest));

        return responseData;
    }

    public ResponseData<DeleteSandboxResponse> stopSandbox(String sandboxId) throws RuntimeException, IOException {
        return execute(sandboxAPI.stopSandbox(sandboxId));
    }

    public ResponseData<SandboxDetailsResponse> getSandbox(String sandboxId) throws RuntimeException, IOException {
        return execute(sandboxAPI.getSandbox(sandboxId));
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
