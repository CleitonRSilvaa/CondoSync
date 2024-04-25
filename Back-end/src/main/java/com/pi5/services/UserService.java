package com.pi5.services;

import com.pi5.models.User;
import com.pi5.repositores.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {


    @Autowired
    private UsersRepository usersRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return usersRepository.findByuserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

    public List<User> getUsers() {
        return usersRepository.findAll();
    }

    public User getUserById(UUID id) {
        return usersRepository.findById(id).orElse(null);
    }

    public User createUser(User user) {

        return usersRepository.save(user);
    }

    public User updateUser(User user) {
        return usersRepository.save(user);
    }

    public User patchUser(User user) {
        return usersRepository.save(user);
    }

    public void deleteUser(User user) {
        usersRepository.delete(user);
    }
}
