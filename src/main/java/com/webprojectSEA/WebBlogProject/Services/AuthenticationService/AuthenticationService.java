package com.webprojectSEA.WebBlogProject.Services.AuthenticationService;

import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {


    String getLoggedInUserNickname(Authentication authentication);
    UserAccount getLoggedInUser(Authentication authentication);
}
