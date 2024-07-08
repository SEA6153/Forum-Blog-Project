package com.webprojectSEA.WebBlogProject.Services.AuthenticationService;

import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserAccountDetailsServiceImpl userDetailsService;
    private final UserAccountRepository userAccountRepository;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    public AuthenticationServiceImpl(UserAccountDetailsServiceImpl userDetailsService,
                                     UserAccountRepository userAccountRepository,
                                     PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String getLoggedInUserNickname(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                return userDetails.getUsername();
            }
        }
        return null;
    }

    @Override
    public UserAccount getLoggedInUser(Authentication authentication) {
        String username = authentication.getName();
        return userAccountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

}
