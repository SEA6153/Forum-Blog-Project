package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Services.AuhtorityServices.AuthorityServiceImpl;
import com.webprojectSEA.WebBlogProject.Model.Authority;
import com.webprojectSEA.WebBlogProject.Model.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthorityController {

    private final AuthorityServiceImpl authorityServiceImpl;

    @Autowired
    public AuthorityController(AuthorityServiceImpl authorityServiceImpl) {
        this.authorityServiceImpl = authorityServiceImpl;
    }

    @PostMapping("/authority")
    public Roles createAuthority(@RequestBody Roles role) {
        Authority authority = authorityServiceImpl.saveAuthority(role);
        return authority.getRoleName();
    }
}
