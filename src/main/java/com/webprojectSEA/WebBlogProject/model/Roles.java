package com.webprojectSEA.WebBlogProject.model;

import lombok.Getter;

@Getter
public enum Roles {

    ADMIN("ADMIN"),
    USER("USER"),
    SUPERUSER("SUPERUSER"),
    UNDEFINED("UNDEFINED");




    private final String authorityRoles;

    Roles(String authorityRoles){
        this.authorityRoles = authorityRoles;
    }


}
