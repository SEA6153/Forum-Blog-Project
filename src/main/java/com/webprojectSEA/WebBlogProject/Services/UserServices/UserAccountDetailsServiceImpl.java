package com.webprojectSEA.WebBlogProject.Services.UserServices;

import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserAccountDetailsServiceImpl implements UserAccountDetailsService {

    private final UserAccountRepository userAccountRepository;

    public UserAccountDetailsServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByEmail(username)
                .or(() -> userAccountRepository.findByNickname(username))
                .orElseThrow(() -> new UsernameNotFoundException("Wrong Username or E-mail"));

        List<GrantedAuthority> grantedAuthorities = account.getRoles()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityRoles()))
                .collect(Collectors.toList());

        return new User(account.getEmail(), account.getPassword(), grantedAuthorities);
    }
}