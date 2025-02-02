package org.example.islab.controller;

import lombok.AllArgsConstructor;
import org.example.islab.configuration.SecurityConfig;
import org.example.islab.configuration.auth.SessionHandler;
import org.example.islab.dto.AuthRequestDTO;
import org.example.islab.entity.AdminRequest;
import org.example.islab.entity.User;
import org.example.islab.entity.UserType;
import org.example.islab.repository.UserRepository;
import org.example.islab.service.AdminRequestService;
import org.example.islab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager manager;
    private final SessionHandler handler;
    private final UserRepository userRepository;
    private final SecurityConfig config;
    private final UserService userService;
    private final AdminRequestService adminRequestService;
    private final SimpMessagingTemplate simpMessagingTemplate;

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
        createNewUser(request);
        return login(request);
    }

    private void createNewUser(final AuthRequestDTO request) {
        User newUser = new User();
        newUser.setLogin(request.getUsername());
        newUser.setPass(config.passwordEncoder().encode(request.getPassword()));
        newUser.setNonExpired(true);
        newUser.setNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        newUser.setEnabled(true);

        if (userService.isFirstUser()) {
            newUser.setUserType(UserType.ADMIN);
        }

        try {
            userRepository.save(newUser);
            if (!userService.isFirstUser() && request.getRequestAdmin() != null && request.getRequestAdmin()) {
                AdminRequest req = adminRequestService.requestRole(newUser);
                simpMessagingTemplate.convertAndSend("/topic/updatedAdminRequest", req);
            }
        } catch (Throwable e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot register user with that username (" + request.getUsername() + ") " +
                            "( / highly likely it already exists).");
        }
    }

    @GetMapping("/whoAmI")
    public ResponseEntity<User> whoAmI() {
        User user = userService.getCurrentUser();
        user.setPass(null);
        return ResponseEntity.ok(user);
    }
}
