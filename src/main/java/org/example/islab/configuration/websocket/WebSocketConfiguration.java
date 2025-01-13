package org.example.islab.configuration.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private final AuthChannelInterceptorAdapter authChannelInterceptorAdapter;

    @Autowired
    public WebSocketConfiguration(AuthChannelInterceptorAdapter authChannelInterceptorAdapter){
        this.authChannelInterceptorAdapter = authChannelInterceptorAdapter;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/ws-api");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket").setAllowedOrigins("http://localhost:4200").withSockJS();
        registry.addEndpoint("/socket").setAllowedOrigins("http://localhost:4200");
    }

    @Override
    public void  configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptorAdapter);
    }

    @Bean("csrfChannelInterceptor")
    ChannelInterceptor noopCsrfChannelInterceptor() {
        return new ChannelInterceptor() {};
    }
}
