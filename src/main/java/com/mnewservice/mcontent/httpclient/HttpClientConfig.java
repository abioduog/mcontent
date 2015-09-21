package com.mnewservice.mcontent.httpclient;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Configuration
public class HttpClientConfig {

    private final PoolingHttpClientConnectionManager connectionManager;

    /* HttpClient instances are thread-safe */
    private CloseableHttpClient httpClient;

    public HttpClientConfig() {
        connectionManager = new PoolingHttpClientConnectionManager();
    }

    @Bean
    public CloseableHttpClient httpClient() {
        if (httpClient == null) {
            httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();
        }
        return httpClient;
    }
}
