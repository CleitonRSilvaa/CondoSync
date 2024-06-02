package com.CondoSync.repositores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.CondoSync.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<User, UUID> {

    Optional<User> findByuserName(String username);

    boolean existsByuserName(String string);

    @Query("SELECT u FROM User u JOIN FETCH u.roles r WHERE r.nome = 'ADMIN'")
    List<User> findAllAdmins();

}
