package com.CondoSync.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.CondoSync.models.DTOs.SubscriptionDTO;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ApiPushManagerService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Async
    public void sendNotification(List<SubscriptionDTO> subscriptions, Payload payload) {
        log.info("Enviando notificação para: " + subscriptions.size() + " dispositivos.");
        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8020/api/v1/notifications/send-notification")
                    .body(Mono.just(new NotificationRequest(subscriptions, payload)), NotificationRequest.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> {
                        log.info("Notificação enviada com sucesso: " + response);
                    })
                    .doOnError(error -> {
                        log.error("Erro ao enviar notificação: ", error);
                    })
                    .subscribe();
        } catch (Exception e) {
            log.error("Erro ao enviar notificação: ", e);
        }
    }

    public static class Payload {
        String title;
        String body;
        String icon;
        String image;
        String badge;
        String tag;
        String url;
        List<Action> actions;
    }

    public static class Action {
        String action;
        String title;
        String icon;
    }

    public static class NotificationRequest {
        List<SubscriptionDTO> subscriptions;
        Payload payload;

        public NotificationRequest(List<SubscriptionDTO> subscriptions, Payload payload) {
            this.subscriptions = subscriptions;
            this.payload = payload;
        }
    }
}