package org.example.islab.controller;

import org.example.islab.configuration.SecurityConfig;
import org.example.islab.configuration.auth.SessionHandler;
import org.example.islab.dto.AuthRequestDTO;
import org.example.islab.entity.User;
import org.example.islab.repository.UserRepository;
import org.example.islab.service.UserService;
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
    private final UserService userService;

    @Autowired
    public AuthController(
            final AuthenticationManager manager,
            final SessionHandler handler,
            final UserRepository repository,
            final SecurityConfig config,
            final UserService userService) {
        this.manager = manager;
        this.handler = handler;
        this.repository = repository;
        this.config = config;
        this.userService = userService;
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody final AuthRequestDTO request) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
        ));

        final String sessionID = handler.register(request.getUsername());
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
    public ResponseEntity<String> registration(@RequestBody final AuthRequestDTO request) {
        User newUser = new User();
        newUser.setLogin(request.getUsername());
        newUser.setPass(config.passwordEncoder().encode(request.getPassword()));
        newUser.setNonExpired(true);
        newUser.setNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        newUser.setEnabled(true);
        try {
            repository.save(newUser);
        } catch (Throwable e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot register user with that username (" + request.getUsername() + ") " +
                            "( / highly likely it already exists).");
        }
        return login(request);
    }

    @GetMapping("/whoAmI")
    public ResponseEntity<User> whoAmI() {
        User user = userService.getCurrentUser();
        user.setPass(null);
        return ResponseEntity.ok(user);
    }
}
