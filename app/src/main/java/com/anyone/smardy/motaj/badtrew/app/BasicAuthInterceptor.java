package com.anyone.smardy.motaj.badtrew.app;

import java.io.IOException;

import io.michaelrocks.paranoid.Obfuscate;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Obfuscate
public class BasicAuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", Credentials.basic("aburabee3", "33ZQZx4wdRHZ33ZQZx4wdRHZ"))
                .build();

        return chain.proceed(authenticatedRequest);
    }
}
