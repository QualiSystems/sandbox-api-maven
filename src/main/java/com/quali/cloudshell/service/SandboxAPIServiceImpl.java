package com.quali.cloudshell.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quali.cloudshell.api.*;
import com.quali.cloudshell.qsExceptions.InvalidApiCallException;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private User user = null;
    private String authToken = null;

    public SandboxAPIServiceImpl(final SandboxServiceConnection connection) throws SandboxApiException {

        user = connection.user;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        boolean isDebug = Boolean.valueOf(System.getenv("CLOUDSHELL_DEBUG"));
        if (isDebug) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }

        if (connection.ignoreSsl) {
            ignoreSsl(builder);
        }

        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);

        SandboxAPIAuthProvider sandboxAPISpecProvider = new SandboxAPIAuthProvider() {
            @Override
            public String getAuthToken() {
                return authToken;
            }

            @Override
            public void loginAndSetAuthToken() throws IOException, SandboxApiException {
                authToken = login().getData();
            }
        };

        SandboxAPIAuthenticator sandboxAPIAuthenticator = new SandboxAPIAuthenticator(sandboxAPISpecProvider);

        builder.addInterceptor(new SandboxAPIRequestInterceptor(sandboxAPISpecProvider));

        builder.authenticator(sandboxAPIAuthenticator);

        OkHttpClient client = builder.build();

        if (connection.serverAddress.isEmpty())
            throw new SandboxApiException("Failed to obtain CloudShell Sandbox API server address, Please validate Sandbox API configuration. ");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(connection.serverAddress)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();


        sandboxAPI = retrofit.create(SandboxAPISpec.class);
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

    public ResponseData<String> login() throws RuntimeException, IOException, SandboxApiException {

        ResponseData<String> responseData = execute(sandboxAPI.login(user), 5);
        if (!responseData.isSuccessful())
            throw new SandboxApiException("Failed to login: " + responseData.getError());
        return responseData;
    }

    public ResponseData<CreateSandboxResponse[]> getBlueprints() throws RuntimeException, IOException, SandboxApiException {
        return execute(sandboxAPI.getBlueprint(), 5);
    }

    public ResponseData<SandboxActivity> getSandboxActivity(String sandboxId, Integer tail , Long from_event_id, String since, Boolean error_only) throws RuntimeException, IOException, SandboxApiException {
        return execute(sandboxAPI.getSandboxActivity(sandboxId, error_only, since, from_event_id, tail), 5);
    }

    public ResponseData<CreateSandboxResponse> createSandbox(String blueprintId, CreateSandboxRequest sandboxRequest) throws RuntimeException, IOException, SandboxApiException {
        return execute(sandboxAPI.createSandbox(blueprintId, sandboxRequest), 0);
    }

    public void stopSandbox(String sandboxId) throws RuntimeException, IOException, SandboxApiException {
        execute(sandboxAPI.stopSandbox(sandboxId), 5);
    }

    public ResponseData<SandboxDetailsResponse> getSandbox(String sandboxId) throws RuntimeException, IOException, SandboxApiException {
        return execute(sandboxAPI.getSandbox(sandboxId), 0);
    }

    private static <T> ResponseData<T> parseResponse(final Response<T> response) throws IOException, SandboxApiException {
        String message = response.message();
        if (!response.isSuccessful()) {
            String error = response.errorBody().string();
            if (error.contains("Invalid Api call")) {
                throw new InvalidApiCallException(response.raw().request().url().toString());
            }
            throw new SandboxApiException(error);
        }
        return ResponseData.ok(response.body(),response.code()).setMessage(message);
    }

    public <T> ResponseData<T> execute(Call<T> call, Integer retries) throws IOException, SandboxApiException {

        String errors = "";
        while (retries>=0) {
            try {
                retries--;
                Response<T> execute = call.clone().execute();
                return parseResponse(execute);
            }
//            catch (IOException ie) {
//                System.out.println(ie.getMessage());
//                errors += ie.getMessage() + "\r\n";
//            }
            catch (SandboxApiException sae) {
                System.out.println(sae.getMessage());
                errors += sae.getMessage() + "\r\n";
                if (!sae.getMessage().contains("Request rate quota has been exceeded"))
                    throw new SandboxApiException(sae.getMessage());
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                errors += e.getMessage() + "\r\n";
            }

            if (retries<=0) {
                throw new SandboxApiException(errors);
            }
            else {
                try {
                    Thread.sleep(1500);
                } catch (Exception e) {
                }
            }
        }

        return null;
    }

}
