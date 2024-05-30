package com.CondoSync.services;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.CondoSync.models.DTOs.SubscriptionDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiPushManagerService {

    @Async
    public void sendNotification(List<SubscriptionDTO> subscriptions, Payload payload) {
        log.info("Enviando notificação para: " + subscriptions.size() + " dispositivos.");
        try {
            NotificationRequest request = new NotificationRequest(subscriptions, payload);
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.postForObject(
                    "https://condosyn.eastus.cloudapp.azure.com:8080/api/v1/notifications/send-notification",
                    request,
                    String.class);
            log.info("Notificação enviada com sucesso: " + response);
        } catch (Exception e) {
            log.error("Erro ao enviar notificação: ", e.fillInStackTrace());
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
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

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Action {
        String action;
        String title;
        String icon;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NotificationRequest {
        List<SubscriptionDTO> subscriptions;
        Payload payload;

        public NotificationRequest(List<SubscriptionDTO> subscriptions, Payload payload) {
            this.subscriptions = subscriptions;
            this.payload = payload;
        }
    }
}