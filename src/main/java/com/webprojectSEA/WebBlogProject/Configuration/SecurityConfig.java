package com.webprojectSEA.WebBlogProject.Configuration;

import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private static final String[] WHITELIST = {
            "/",
            "/register",
            "/posts/{id}",
            "/css/**",
            "/posts"
    };

    private final UserAccountDetailsServiceImpl userDetailsService;
    private final AuthSuccessHandler authSuccessHandler;

    public SecurityConfig(UserAccountDetailsServiceImpl userDetailsService, AuthSuccessHandler authSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.authSuccessHandler = authSuccessHandler;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .and()
                .authorizeRequests()
                .antMatchers(WHITELIST).permitAll()
                .antMatchers(HttpMethod.GET, "/posts/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(authSuccessHandler)
                .failureUrl("/login?error").permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .and()
                .httpBasic();

        return http.build();
    }
}