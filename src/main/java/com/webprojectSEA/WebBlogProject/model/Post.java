
//DEFINE POSTING PROPERTIES

package com.webprojectSEA.WebBlogProject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Category category;


    private String title;

    @Column(columnDefinition = "TEXT")
    private String explanation;


    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private UserAccount userAccount;


}
