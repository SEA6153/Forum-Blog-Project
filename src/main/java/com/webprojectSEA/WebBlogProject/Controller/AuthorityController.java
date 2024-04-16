package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Services.AuthorityServiceImpl;
import com.webprojectSEA.WebBlogProject.model.Authority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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