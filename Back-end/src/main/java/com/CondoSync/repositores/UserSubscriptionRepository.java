package com.CondoSync.repositores;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.PushSubscription;

import java.util.Enumeration;
import java.util.List;
import com.CondoSync.models.User;

public interface UserSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {

  List<PushSubscription> findAllByUserId(UUID userId);

}
