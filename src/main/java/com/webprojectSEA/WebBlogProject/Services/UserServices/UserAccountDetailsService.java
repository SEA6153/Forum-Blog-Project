package com.webprojectSEA.WebBlogProject.Services.UserServices;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserAccountDetailsService extends org.springframework.security.core.userdetails.UserDetailsService{

    UserDetails loadUserByUsername(String username);
}
