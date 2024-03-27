package com.example.jwtTest2.config;


import com.example.jwtTest2.config.auth.CustomUserDetailsService;
import com.example.jwtTest2.config.filter.CustomJwtFilter;
import com.example.jwtTest2.config.jwt.JwtFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFunction jwtFunction;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        sharedObject.userDetailsService(this.customUserDetailsService);
        AuthenticationManager authenticationManager = sharedObject.build();
        http.authenticationManager(authenticationManager);

        http.csrf(cs -> cs.disable());
        http.addFilter(corsFilter);

        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin(f -> f.disable());
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.addFilterBefore(new CustomJwtFilter(customUserDetailsService, jwtFunction), UsernamePasswordAuthenticationFilter.class);


        http.authorizeHttpRequests(authorize -> {
            authorize
                    .requestMatchers("/api/v1/user/**")
                    .hasAnyRole("USER", "MANAGER", "ADMIN") // 자동으로 앞에 "ROLE_"을 추가해서 체크한다.
                    .requestMatchers("/api/v1/manager/**")
                    .hasAnyRole("MANAGER", "ADMIN")
                    .requestMatchers("/api/v1/admin/**")
                    .hasAnyRole("ADMIN")
                    .anyRequest().permitAll();}
        );

        return http.build();
    }
}
