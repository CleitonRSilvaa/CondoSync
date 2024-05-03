package com.CondoSync.repositores;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.PushSubscription;

public interface UserSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {

}
