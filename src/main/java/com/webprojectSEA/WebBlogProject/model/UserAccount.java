package com.webprojectSEA.WebBlogProject.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String firstName;
    private String lastName;
    private String password;
    private String nickname;
    private String email;

    @OneToMany(mappedBy = "userAccount")
    private List<Post> posts;



}
