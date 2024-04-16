package com.webprojectSEA.WebBlogProject.Services;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserAccountDetailsService extends org.springframework.security.core.userdetails.UserDetailsService, UserDetails{

    UserDetails loadUserByUsername(String username);

    @Override
    boolean isAccountNonExpired();

    @Override
    boolean isAccountNonLocked();

    @Override
    boolean isCredentialsNonExpired();

    @Override
    boolean isEnabled();
}
