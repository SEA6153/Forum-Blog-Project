package com.webprojectSEA.WebBlogProject.Services.UserServices;

import com.webprojectSEA.WebBlogProject.model.Roles;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class UserAccountDetailsImpl implements UserAccountDetails {

    @Getter
    @Setter
    private UserAccount userAccount;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Roles> roles = userAccount.getRoles();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roles.toString());


        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return userAccount.getPassword();
    }

    @Override
    public String getUsername() {
        return userAccount.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userAccount.isAccountNonLocked();
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
