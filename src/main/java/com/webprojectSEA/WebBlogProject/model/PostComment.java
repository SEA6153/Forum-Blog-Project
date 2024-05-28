package com.webprojectSEA.WebBlogProject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userAccount_id")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    private Post post;
    @Transient
    private String postTitle;

    private LocalDateTime createdAt;
}
