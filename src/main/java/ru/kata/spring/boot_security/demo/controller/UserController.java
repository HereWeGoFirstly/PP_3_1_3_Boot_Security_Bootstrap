package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Controller
public class UserController {

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/user")
    public String user(Model model) {
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/access-error")
    public String accessError() {
        return "access-error";
    }

    @GetMapping("/admin")
    public String list(Model model) {
        model.addAttribute("users", userService.listUsers());
        return "users/list";
    }

    @GetMapping("/admin/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.setOfRoles());
        return "users/new";
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable("id") int id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute("user") User user
            , @RequestParam Map<String, String> form) {
        Set<Role> roleSet = roleService.setOfRoles();
        Set<String> stringRoleSet = form.keySet();
        for (Role role : roleSet) {
            if (stringRoleSet.contains(role.getRole())) {
                user.addRole(role);
            }
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.add(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("roles", roleService.setOfRoles());
        return "users/edit";
    }

    @PostMapping("/admin/{id}")
    public String update(@ModelAttribute("user") User user
            , @PathVariable("id") int id
            , @RequestParam Map<String, String> form) {
        Set<Role> roleSet = roleService.setOfRoles();
        Set<String> stringRoleSet = form.keySet();
        for (Role role : roleSet) {
            if (stringRoleSet.contains(role.getRole())) {
                user.addRole(role);
            }
        }
        userService.update(id, user);
        return "redirect:/admin";
    }
}