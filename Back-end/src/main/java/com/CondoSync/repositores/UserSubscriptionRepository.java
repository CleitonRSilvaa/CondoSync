package com.CondoSync.repositores;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CondoSync.models.PushSubscription;

import java.util.List;

public interface UserSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {

  List<PushSubscription> findAllByUserId(UUID userId);

  List<PushSubscription> findAllByUserStatus(Boolean status);

  @Query("SELECT ps FROM PushSubscription ps " +
      "JOIN ps.user u " +
      "JOIN u.roles r " +
      "WHERE u.status = true AND r.nome = :roleName")
  List<PushSubscription> findSubscriptionsByUserStatusAndRole(@Param("roleName") String roleName);

}
