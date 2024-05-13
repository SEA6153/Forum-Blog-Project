package com.webprojectSEA.WebBlogProject.Services.UserServices;

import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountDetails;
import com.webprojectSEA.WebBlogProject.model.Roles;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;

public class UserAccountDetailsImpl implements UserAccountDetails {

    @Getter
    @Setter
    private UserAccount userAccount;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Roles roles = userAccount.getRole();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roles.getAuthorityRoles());

        return Arrays.asList(authority);
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
