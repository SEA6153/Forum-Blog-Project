package com.webprojectSEA.WebBlogProject.Services.AuhtorityServices;

import com.webprojectSEA.WebBlogProject.Model.Authority;
import com.webprojectSEA.WebBlogProject.Model.Roles;
import com.webprojectSEA.WebBlogProject.Repostories.UserAuthorityRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final UserAuthorityRepository authorityRepository;


    public AuthorityServiceImpl(UserAuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }


    @Override
    public Authority saveAuthority(Roles authorityRoles) {
        Authority authority = new Authority(authorityRoles);
        return authorityRepository.save(authority);
    }

}
