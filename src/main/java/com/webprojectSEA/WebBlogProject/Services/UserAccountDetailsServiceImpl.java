package com.webprojectSEA.WebBlogProject.Services;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class UserAccountDetailsServiceImpl implements UserAccountDetailsService {

    private final UserAccountServiceImpl userAccountServiceImpl;
    private final UserAccountRepository userAccountRepository;
    private UserAccount userAccount;
    private final LoginAttemptServiceImpl loginAttemptService;

    public UserAccountDetailsServiceImpl(UserAccountServiceImpl userAccountServiceImpl, UserAccountRepository userAccountRepository, LoginAttemptServiceImpl loginAttemptService) {
        this.userAccountServiceImpl = userAccountServiceImpl;
        this.userAccountRepository = userAccountRepository;
        this.loginAttemptService = loginAttemptService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAccount> optionalUserAccountFindByEmail = userAccountRepository.findByEmail(username);
        Optional<UserAccount> optionalUserAccountFindByNickname = userAccountRepository.findByNickname(username);
        if (optionalUserAccountFindByEmail.isEmpty() && optionalUserAccountFindByNickname.isEmpty()) {
            throw new UsernameNotFoundException("Wrong Username or E-mail");
        } else if (optionalUserAccountFindByEmail.isPresent()) {
            UserAccount account = optionalUserAccountFindByEmail.get();
            List<GrantedAuthority> grantedAuthorities = account
                    .getAuthoritySet()
                    .stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                    .collect(Collectors.toList());
            return new User(account.getEmail(),
                    account.getPassword(), grantedAuthorities);
        } else {
            UserAccount account1 = optionalUserAccountFindByNickname.get();
            List<GrantedAuthority> grantedAuthorities = account1
                    .getAuthoritySet()
                    .stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                    .collect(Collectors.toList());
            return new User(account1.getNickname(),
                    account1.getPassword(), grantedAuthorities);
        }

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return userAccount.isEnabled();
    }

}

