package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    public String getLoginPage(){
        return "login";
    }






    public UserDetails loginControlwithUserName(String username){
       return userDetailsService.loadUserByUsername(username);
    }

    @GetMapping("user/id")
    public Long getLoggedInUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Optional<UserAccount> user = userAccountRepository.findByNickname(username);

        if (user.isPresent()) {
            if (!user.get().isEnabled()) {
                throw new RuntimeException("User is not active");
            }
            return user.get().getId();
        } else {
            throw new RuntimeException("User not found with username: " + username);
        }
    }

}
