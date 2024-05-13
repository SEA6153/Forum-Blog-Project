package com.webprojectSEA.WebBlogProject.Services.AuhtServices;

import com.webprojectSEA.WebBlogProject.Repostories.UserAuthorityRepository;
import com.webprojectSEA.WebBlogProject.Services.AuhtServices.AuthorityService;
import com.webprojectSEA.WebBlogProject.model.Authority;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {


    private final UserAuthorityRepository authorityRepository;

    public AuthorityServiceImpl(UserAuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public Authority saveAuthority(String name) {
        Authority authority = new Authority(name);
        return authorityRepository.save(authority);
    }
}
