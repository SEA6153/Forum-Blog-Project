package com.webprojectSEA.WebBlogProject.Services;

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

@Component("userDetailsService")
public class UserAccountDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserAccountService userAccountService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserAccount> optionalUserAccount = userAccountService.findByEmail(email);
        if(!optionalUserAccount.isPresent()){
            throw new UsernameNotFoundException("Account not found!");
        }
        else{
            UserAccount account = optionalUserAccount.get();
            List<GrantedAuthority> grantedAuthorities = account
                    .getAuthoritySet()
                    .stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                    .collect(Collectors.toList());
            return new User(account.getEmail(),
                    account.getPassword(), grantedAuthorities);
        }

    }
}
