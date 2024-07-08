package com.webprojectSEA.WebBlogProject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class UserLikeDislike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserAccount user;

    @ManyToOne
    private Post post;

    private boolean isLiked;
    private boolean isDisliked;
}
