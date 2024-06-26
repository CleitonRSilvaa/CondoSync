package com.CondoSync.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.CondoSync.models.PushSubscription;
import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.SubscriptionDTO;
import com.CondoSync.models.DTOs.UserSubscriptionDTO;
import com.CondoSync.models.DTOs.SubscriptionDTO.Keys;
import com.CondoSync.repositores.UserSubscriptionRepository;

@Service
public class UserSubscriptionService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    public ResponseEntity<?> saveSubscription(UserSubscriptionDTO userSubscriptionDTO) {

        User user = userService.findById(userSubscriptionDTO.getUser_id())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var subscribe = new PushSubscription();

        subscribe.setEndpoint(userSubscriptionDTO.getEndpoint());
        subscribe.setExpirationTime(userSubscriptionDTO.getExpirationTime());
        subscribe.setP256dh(userSubscriptionDTO.getKeys().getP256dh());
        subscribe.setAuth(userSubscriptionDTO.getKeys().getAuth());
        subscribe.setUser(user);

        userSubscriptionRepository.save(subscribe);

        return ResponseEntity.status(201).build();
    }

    public ResponseEntity<?> deleteSubscription(UserSubscriptionDTO userSubscriptionDTO) {
        return null;
    }

    public List<SubscriptionDTO> getSubscriptions(UUID userId) {

        List<SubscriptionDTO> subs = new ArrayList<>();

        var subscriptions = (List<PushSubscription>) userSubscriptionRepository.findAllByUserId(userId);

        for (PushSubscription pushSubscription : subscriptions) {
            SubscriptionDTO sub = new SubscriptionDTO();
            sub.setEndpoint(pushSubscription.getEndpoint());
            sub.setExpirationTime(pushSubscription.getExpirationTime());
            sub.setKeys(new Keys(pushSubscription.getP256dh(), pushSubscription.getAuth()));
            subs.add(sub);
        }

        return subs;

    }

    public List<SubscriptionDTO> findAllIsActive() {

        var subscriptions = userSubscriptionRepository.findAllByUserStatus(true);
        List<SubscriptionDTO> subs = new ArrayList<>();

        for (PushSubscription pushSubscription : subscriptions) {
            SubscriptionDTO sub = new SubscriptionDTO();
            sub.setEndpoint(pushSubscription.getEndpoint());
            sub.setExpirationTime(pushSubscription.getExpirationTime());
            sub.setKeys(new Keys(pushSubscription.getP256dh(), pushSubscription.getAuth()));
            subs.add(sub);
        }

        return subs;

    }

    public List<SubscriptionDTO> findSubscriptionsByUserStatusAndRole(String role) {
        var subscriptions = userSubscriptionRepository.findSubscriptionsByUserStatusAndRole(role);
        List<SubscriptionDTO> subs = new ArrayList<>();
        for (PushSubscription pushSubscription : subscriptions) {
            SubscriptionDTO sub = new SubscriptionDTO();
            sub.setEndpoint(pushSubscription.getEndpoint());
            sub.setExpirationTime(pushSubscription.getExpirationTime());
            sub.setKeys(new Keys(pushSubscription.getP256dh(), pushSubscription.getAuth()));
            subs.add(sub);
        }
        return subs;
    }

}
