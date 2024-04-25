package com.pi5.repositores;

import com.pi5.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<User, UUID> {


    Optional<User> findByuserName(String username);



}
