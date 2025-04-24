package ru.onliver.room_manager.config;

import io.livekit.server.WebhookReceiver;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.livekit.server.RoomServiceClient;

@Configuration
public class LiveKitConfig {
    @Value("${livekit.apiKey}") 
    String apiKey;
    @Value("${livekit.secret}") 
    String apiSecret;
    @Value("${livekit.host}")
    String host;

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getHost() {
        return host;
    }

    @Bean
    public WebhookReceiver webhookReceiver() {
        return new WebhookReceiver(apiKey, apiSecret);
    }

    @Bean
    public RoomServiceClient roomServiceClient() {
        return RoomServiceClient.createClient(host, apiKey, apiSecret, () -> new OkHttpClient());
    }
}
