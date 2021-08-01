package com.gadihkaratau.lamejorescancionesreik.restApi;

import android.util.Log;

import com.gadihkaratau.lamejorescancionesreik.BuildConfig;
import com.gadihkaratau.lamejorescancionesreik.application.MyApplication;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String TAG = ApiClient.class.getSimpleName();
    private static final String BASE_URL = BuildConfig.BASE_URL;
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_PRAGMA = "Pragma";
    private static final long cacheSize = 500 * 1024 * 1024; // 500 MB

    private static Retrofit getClient(int maxAge) {

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient(maxAge))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient okHttpClient(int maxAge) {
        return new OkHttpClient.Builder()
                .cache(cache())
                .addInterceptor(httpLoggingInterceptor()) // used if network off OR on
                .addNetworkInterceptor(networkInterceptor(maxAge)) // only used when network is on
                .addInterceptor(offlineInterceptor())
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private static Cache cache() {
        return new Cache(new File(Objects.requireNonNull(MyApplication.Companion.getInstance()).getCacheDir(), BuildConfig.APPLICATION_ID), cacheSize);
    }

    public static ApiInterface serviceApi(int maxAge) {
        return getClient(maxAge).create(ApiInterface.class);
    }

    /**
     * This interceptor will be called both if the network is available and if the network is not available
     *
     * @return
     */
    private static Interceptor offlineInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.d(TAG, "offline interceptor: called.");
                Request request = chain.request();

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(1000, TimeUnit.DAYS)
                        .build();

                // prevent caching when network is on. For that we use the "networkInterceptor"
                if (!MyApplication.Companion.hasNetwork()) {

                    request = request.newBuilder()
                            .removeHeader(HEADER_PRAGMA)
                            .removeHeader(HEADER_CACHE_CONTROL)
                            .cacheControl(cacheControl)
                            .build();
                }


                return chain.proceed(request);
            }
        };
    }

    /**
     * This interceptor will be called ONLY if the network is available
     *
     * @return
     */
    private static Interceptor networkInterceptor(int maxAge) {
        return new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Log.d(TAG, "network interceptor: called.");

                Response response = chain.proceed(chain.request());

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(maxAge, TimeUnit.SECONDS)
                        .build();

                return response.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    private static HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
//                Log.e(TAG, "log: http log: " + message);
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }


}
