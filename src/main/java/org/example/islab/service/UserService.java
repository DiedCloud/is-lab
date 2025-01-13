package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.example.islab.entity.User;
import org.example.islab.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username);
    }

    public User getByLogin(String login) {
        User user = userRepository.findByLogin(login);
        if (user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public User getCurrentUser() {
        return getByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
