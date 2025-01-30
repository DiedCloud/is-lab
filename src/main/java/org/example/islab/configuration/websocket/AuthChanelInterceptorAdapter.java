package org.example.islab.configuration.websocket;

import lombok.AllArgsConstructor;
import org.example.islab.configuration.auth.SessionHandler;
import org.example.islab.service.UserService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthChanelInterceptorAdapter implements ChannelInterceptor {
    private final SessionHandler sessionHandler;
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null){
            throw new BadCredentialsException("");
        }

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token == null){
                throw new BadCredentialsException("");
            }
            String username = sessionHandler.getUsernameForSession(token);
            if (username == null){
                throw new BadCredentialsException("");
            }
            UserDetails details = userService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken user  = UsernamePasswordAuthenticationToken.authenticated(
                    details.getUsername(), details.getPassword(), details.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(user);
            accessor.setUser(user);
        }
        return message;
    }
}
