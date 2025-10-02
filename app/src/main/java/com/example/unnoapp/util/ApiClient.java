package com.example.unnoapp.util;

import android.os.Build;

import com.example.unnoapp.BuildConfig;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    // Detecta se o app está rodando em emulador ou dispositivo físico
    private static String getBaseUrl() {
        if (android.os.Build.FINGERPRINT.contains("generic")
                || android.os.Build.FINGERPRINT.contains("sdk_gphone")) {
            // Está rodando no emulador
            return BuildConfig.BASE_URL_EMULADOR;
        } else {
            // Está rodando em um celular físico
            return BuildConfig.BASE_URL_FISICO;
        }
    }

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            String baseUrl = determineBaseUrl();

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging);

            // Se estiver usando Supabase, injeta headers obrigatórios
            if (BuildConfig.USE_SUPABASE) {
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder rb = original.newBuilder()
                                .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                                .header("Authorization", "Bearer " + BuildConfig.SUPABASE_ANON_KEY)
                                .header("Accept", "application/json");
                        Request request = rb.method(original.method(), original.body()).build();
                        return chain.proceed(request);
                    }
                });
            }

            OkHttpClient client = httpClient.build();

            // garante trailing slash
            if (!baseUrl.endsWith("/")) baseUrl = baseUrl + "/";

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static String determineBaseUrl() {
        if (BuildConfig.USE_SUPABASE) {
            return BuildConfig.SUPABASE_URL;
        } else {
            // mantém seu comportamento: se estiver em emulador, usa BASE_URL_EMULADOR, senão FISICO
            return isEmulator() ? BuildConfig.BASE_URL_EMULADOR : BuildConfig.BASE_URL_FISICO;
        }
    }

    // Detecção razoável de emulador (mesmo método que muitos projetos usam)
    private static boolean isEmulator() {
        final String fingerprint = Build.FINGERPRINT != null ? Build.FINGERPRINT : "";
        final String model = Build.MODEL != null ? Build.MODEL : "";
        final String manufacturer = Build.MANUFACTURER != null ? Build.MANUFACTURER : "";
        final String brand = Build.BRAND != null ? Build.BRAND : "";
        final String device = Build.DEVICE != null ? Build.DEVICE : "";
        final String product = Build.PRODUCT != null ? Build.PRODUCT : "";

        return fingerprint.startsWith("generic") ||
                fingerprint.startsWith("unknown") ||
                model.contains("google_sdk") ||
                model.contains("Emulator") ||
                model.contains("Android SDK built for x86") ||
                manufacturer.contains("Genymotion") ||
                (brand.startsWith("generic") && device.startsWith("generic")) ||
                "google_sdk".equals(product);
    }
}

