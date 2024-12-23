package org.example.islab.configuration;

import jakarta.servlet.http.HttpServletResponse;
import org.example.islab.configuration.auth.SessionFilter;
import org.example.islab.service.UserService;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import static org.springframework.security.config.Customizer.withDefaults;

@ToString
@EqualsAndHashCode
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;
    private final SessionFilter sessionFilter;
    private final MvcRequestMatcher.Builder mvc;

    @Autowired
    public SecurityConfig(
            final UserService userService,
            final SessionFilter sessionFilter,
            final MvcRequestMatcher.Builder mvc
    ) {
        this.userService = userService;
        this.sessionFilter = sessionFilter;
        this.mvc = mvc;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable);
        http.exceptionHandling(eh ->
                eh.authenticationEntryPoint((rq, rs, ex) ->
                        rs.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getLocalizedMessage())
                )
        );

        http.csrf(csrf ->
                csrf.ignoringRequestMatchers("/**")
        );
        http.headers(headersConfigurer ->
                headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        CorsFilter filter = new CorsFilter(source);


        http.addFilter(filter);
        http.addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests((ar) -> {
            ar.requestMatchers(mvc.pattern("/auth/login")).permitAll();
            ar.requestMatchers(mvc.pattern("/auth/registration")).permitAll();
            ar.anyRequest().authenticated();
        }).httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder messages = new MessageMatcherDelegatingAuthorizationManager.Builder();
        messages.simpTypeMatchers(
                SimpMessageType.CONNECT,
                SimpMessageType.HEARTBEAT,
                SimpMessageType.UNSUBSCRIBE,
                SimpMessageType.DISCONNECT
        ).permitAll().anyMessage().authenticated();
        return messages.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
