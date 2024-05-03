package com.CondoSync.models;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "subscriptions_navegation")
public class PushSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subscription_id")
    private UUID id;

    @Column(nullable = false)

    private String endpoint;

    @Column(nullable = true)
    private String expirationTime;

    @Column(nullable = false)
    private String p256dh;

    @Column(nullable = false)
    private String auth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false, unique = true, length = 500)
    @Size(max = 500)
    private String hashKey;

    @CreationTimestamp
    private Instant creation;

    @UpdateTimestamp
    private Instant upudate;

    @PrePersist
    public void setHashKey() {
        this.hashKey = String.join(auth, endpoint, p256dh).toLowerCase();
    }

    @Override
    public String toString() {
        return "PushSubscription [auth=" + auth + ", creation=" + creation + ", endpoint=" + endpoint
                + ", expirationTime="
                + expirationTime + ", hashKey=" + hashKey + ", id=" + id + ", p256dh=" + p256dh + ", upudate=" + upudate
                + "]";
    }

}