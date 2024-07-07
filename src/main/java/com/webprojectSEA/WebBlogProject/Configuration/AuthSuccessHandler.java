package com.webprojectSEA.WebBlogProject.Configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class AuthSuccessHandler  implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

      Set<String> AuthRoles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if(AuthRoles.contains("ROLE_ADMIN")){
            response.sendRedirect("/admin/profile");
        }else{
            response.sendRedirect("/");
        }
    }

}
