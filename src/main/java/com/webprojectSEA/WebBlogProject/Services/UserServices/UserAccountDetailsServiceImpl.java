package com.webprojectSEA.WebBlogProject.Services.UserServices;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserAccountDetailsServiceImpl implements UserAccountDetailsService {

    @Autowired
    private UserAccountRepository userAccountRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAccount> optionalUserAccountFindByEmail = userAccountRepository.findByEmail(username);
        Optional<UserAccount> optionalUserAccountFindByNickname = userAccountRepository.findByNickname(username);
        if (optionalUserAccountFindByEmail.isEmpty() && optionalUserAccountFindByNickname.isEmpty()) {
            throw new UsernameNotFoundException("Wrong Username or E-mail");
        } else if (optionalUserAccountFindByEmail.isPresent()) {
            UserAccount account = optionalUserAccountFindByEmail.get();
            List<GrantedAuthority> grantedAuthorities = account
                    .getRoles()
                    .stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityRoles()))
                    .collect(Collectors.toList());
            return new User(account.getEmail(),
                    account.getPassword(), grantedAuthorities);
        } else {
            UserAccount account1 = optionalUserAccountFindByNickname.get();
            List<GrantedAuthority> grantedAuthorities = account1
                    .getRoles()
                    .stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityRoles()))
                    .collect(Collectors.toList());
            return new User(account1.getNickname(),
                    account1.getPassword(), grantedAuthorities);
        }

    }
}

