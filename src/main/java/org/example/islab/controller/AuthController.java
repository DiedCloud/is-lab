package org.example.islab.controller;

import org.example.islab.configuration.SecurityConfig;
import org.example.islab.configuration.auth.SessionHandler;
import org.example.islab.entity.User;
import org.example.islab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager manager;
    private final SessionHandler handler;
    private final UserRepository repository;
    private final SecurityConfig config;

    @Autowired
    public AuthController(
            final AuthenticationManager manager,
            final SessionHandler handler,
            final UserRepository repository,
            final SecurityConfig config
    ) {
        this.manager = manager;
        this.handler = handler;
        this.repository = repository;
        this.config = config;
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody final String username,
            @RequestBody final String password
    ) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(
                username, password
        ));

        final String sessionID = handler.register(username);
        return ResponseEntity.ok(sessionID);
    }

    @CrossOrigin
    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authentication) {
        handler.invalidate(authentication);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @PostMapping("/registration")
    public ResponseEntity<String> registration(
            @RequestBody final String username,
            @RequestBody final String password
    ) {
        User newUser = new User();
        newUser.setLogin(username);
        newUser.setPass(config.passwordEncoder().encode(password));
        newUser.setNonExpired(true);
        newUser.setNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        newUser.setEnabled(true);
        try {
            repository.save(newUser);
        } catch (Throwable e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot register user with that username (" + username + ") " +
                            "( / highly likely it already exists).");
        }
        return login(username, password);
    }
}
