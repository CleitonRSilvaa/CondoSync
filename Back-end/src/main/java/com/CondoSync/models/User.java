package com.CondoSync.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Full name is required")
    @Column(length = 150, nullable = false)
    private String fullName;

    @NotBlank(message = "Username is required")
    @Column(length = 50, nullable = false, unique = true)
    private String userName;

    @NonNull
    @Column(length = 500, nullable = false)
    @JsonIgnore
    private String hashPassword;

    private LocalDateTime datahashSenhaUpdate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<PushSubscription> subscriptions;

    @CreationTimestamp
    private Instant creation;

    @UpdateTimestamp
    private Instant upudate;

    private boolean status;

    private boolean inativa;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {

        return this.status;
    }

    @Override
    public boolean isEnabled() {
        return this.status;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        // return this.datahashSenhaUpdate == null
        // || this.datahashSenhaUpdate.isBefore(LocalDateTime.now().minusMonths(3));
        return true;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.hashPassword;
    }

    @Override
    public String toString() {
        return "User [creation=" + creation + ", datahashSenhaUpdate=" +
                datahashSenhaUpdate + ", fullName=" + fullName
                + ", hashPassword=" + hashPassword + ", id=" + id + ", inativa=" + inativa +
                ", roles=" + roles.size()
                + ", status="
                + status + ", subscriptions=" + subscriptions.size() + ", upudate=" + upudate
                + ", userName=" + userName
                + "]";
    }

}
