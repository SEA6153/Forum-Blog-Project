package com.webprojectSEA.WebBlogProject.Services.AuhtServices;

import com.webprojectSEA.WebBlogProject.Model.Authority;
import com.webprojectSEA.WebBlogProject.Model.Roles;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserAuthorityRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final UserAuthorityRepository authorityRepository;
    private final UserDetailsService userDetailsService;
    private final UserAccountRepository userAccountRepository;
    private final AuthenticationManager authenticationManager;

    public AuthorityServiceImpl(UserAuthorityRepository authorityRepository, UserDetailsService userDetailsService, UserAccountRepository userAccountRepository, AuthenticationManager authenticationManager) {
        this.authorityRepository = authorityRepository;
        this.userDetailsService = userDetailsService;
        this.userAccountRepository = userAccountRepository;
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authority saveAuthority(Roles authorityRoles) {
        Authority authority = new Authority(authorityRoles);
        return authorityRepository.save(authority);
    }
    public void authenticate(String emailOrUsername, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(emailOrUsername);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public Long getLoggedInUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String usernameOrEmail = userDetails.getUsername();

        Optional<UserAccount> user = userAccountRepository.findByEmail(usernameOrEmail)
                .or(() -> userAccountRepository.findByNickname(usernameOrEmail));

        if (user.isEmpty()) {
            throw new RuntimeException("User not found with username or email: " + usernameOrEmail);
        }

        UserAccount userAccount = user.get();

        if (!userAccount.isEnabled()) {
            throw new RuntimeException("User is not active");
        }

        return userAccount.getId();
    }

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

    public UserAccount getLoggedInUser(Authentication authentication) {
        String username = authentication.getName();
        return userAccountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
