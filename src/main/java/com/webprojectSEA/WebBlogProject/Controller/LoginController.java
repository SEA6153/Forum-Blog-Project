package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class LoginController {

    private final UserDetailsService userDetailsService;
    private final UserAccountRepository userAccountRepository;

    public LoginController(UserDetailsService userDetailsService, UserAccountRepository userAccountRepository) {
        this.userDetailsService = userDetailsService;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("user/id")
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
