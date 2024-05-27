package com.CondoSync.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.CondoSync.models.DTOs.SubscripitionDTO;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ApiPushManagerService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<String> getDataFromExternalApi() {
        return webClientBuilder.build()
                .get()
                .uri("http://external-api.com/data")
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> postDataToExternalApi(Object requestBody) {
        return webClientBuilder.build()
                .post()
                .uri("http://external-api.com/data")
                .body(Mono.just(requestBody), Object.class)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Async
    public void sendNotification(List<SubscripitionDTO> subscriptions, String payload) {
        webClientBuilder.build()
                .post()
                .uri("http://localhost:8020/api/v1/notifications/send-notification")
                .body(Mono.just(subscriptions), List.class)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> {
                    log.info("Notificação enviada com sucesso: " + response);
                })
                .doOnError(error -> {

                    log.error("Erro ao enviar notificação: " + error.getMessage());
                })
                .subscribe();
    }
    // publc static record Payload(String title, String body, String icon, String
    // image, String tag, String url, String requireInteraction, String renotify,
    // String silent, String vibrate, String dir, String lang, String timestamp,
    // String data) {

    // }

}
