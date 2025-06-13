package com.peakmeshop.common.config;

import com.peakmeshop.common.event.CustomAuthenticationFailureHandler;
import com.peakmeshop.common.event.CustomAuthenticationSuccessHandler;
import com.peakmeshop.common.event.CustomLogoutSuccessHandler;
import com.peakmeshop.common.security.CustomUserDetailsService;
import com.peakmeshop.common.security.CustomPersistentTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFailureHandler authenticationFailureHandler;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/mypage/**").authenticated()
                        .requestMatchers("/cart/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/checkout/**").authenticated()
                        .requestMatchers("/**").permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/auth/login")
                        .defaultSuccessUrl("/")
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(86400)  // 24시간
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?expired=true")
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")  // API 전체에 대해 CSRF 비활성화
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

