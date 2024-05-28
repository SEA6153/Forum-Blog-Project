package com.webprojectSEA.WebBlogProject.model;

import lombok.Getter;

@Getter
public enum Roles {

    ROLE_ADMIN("ADMIN"),
    ROLE_USER("User"),
    ROLE_SUPERUSER("SUPERUSER"),
    ROLE_MODERATOR("MODERATOR"),
    ROLE_UNDEFINED("UNDEFINED");




    private final String authorityRoles;

    Roles(String authorityRoles){
        this.authorityRoles = authorityRoles;
    }

    @Override
    public String toString() {
        return "Your Role: "
                 + authorityRoles;
    }
}
