package com.CondoSync.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.CondoSync.models.User;
import com.CondoSync.services.UserService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@Valid
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        return userService.updateUser(user);
    }

    @PatchMapping
    public User patchUser(@RequestBody @Valid User user) {
        return userService.patchUser(user);
    }

    @DeleteMapping
    public void deleteUser(@RequestBody @Valid User user) {
        userService.deleteUser(user);

    }

}
