package com.CondoSync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.CondoSync.models.PushSubscription;
import com.CondoSync.models.User;
import com.CondoSync.models.DTOs.UserSubscriptionDTO;
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
        // subscribe.setHashKey(subscribe.getHashKey());
        userSubscriptionRepository.save(subscribe);

        return ResponseEntity.status(201).build();
    }

}
