package com.CondoSync.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.Role;
import com.CondoSync.services.RoleService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("api/v1/roles")
@Validated
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Role> getRoles() {
        return roleService.getRoles();
    }

    @GetMapping("/id")
    public Role getRoleById(@RequestParam Long id) {
        return roleService.getRoleById(id);
    }

    @GetMapping("/nome")
    public Role getRoleByNome(@RequestParam String nome) {
        return roleService.getRoleByNome(nome);
    }

    @PostMapping()
    public Role createRole(@RequestBody @Valid Role role) {
        return roleService.createRole(role);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Role updateRole(@RequestBody @Valid Role role) {
        return roleService.updateRole(role);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public Role patchRole(@RequestBody @Valid Role role) {
        return roleService.patchRole(role);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteRole(@RequestBody @Valid Role role) {
        roleService.deleteRole(role);
    }

}
