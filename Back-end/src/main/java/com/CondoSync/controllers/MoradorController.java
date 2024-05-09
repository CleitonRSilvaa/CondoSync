package com.CondoSync.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.User;
import com.CondoSync.services.MoradorService;
import com.CondoSync.services.RoleService;
import com.CondoSync.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("api/v1/morador")
public class MoradorController {

    @Autowired
    private MoradorService moradorService;

}
