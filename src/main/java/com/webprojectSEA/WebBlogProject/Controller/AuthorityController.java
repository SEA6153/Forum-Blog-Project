package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Services.AuhtServices.AuthorityServiceImpl;
import com.webprojectSEA.WebBlogProject.model.Authority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthorityController {


    private final AuthorityServiceImpl authorityServiceImpl;

    public AuthorityController(AuthorityServiceImpl authorityServiceImpl) {
        this.authorityServiceImpl = authorityServiceImpl;
    }

    @PostMapping("/authority")
    public Authority createAuthority(@RequestBody String name) {
        return authorityServiceImpl.saveAuthority(name);
    }
}