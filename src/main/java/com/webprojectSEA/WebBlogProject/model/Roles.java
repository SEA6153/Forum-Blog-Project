package com.webprojectSEA.WebBlogProject.model;

import lombok.Getter;

@Getter
public enum Roles {

    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER"),
    ROLE_SUPERUSER("ROLE_SUPERUSER"),
    ROLE_MODERATOR("ROLE_MODERATOR"),
    ROLE_UNDEFINED("ROLE_UNDEFINED");




    private final String authorityRoles;

    Roles(String authorityRoles){
        this.authorityRoles = authorityRoles;
    }


}
