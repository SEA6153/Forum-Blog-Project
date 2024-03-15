
//DEFINE POSTING PROPERTIES

package com.webprojectSEA.WebBlogProject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "Category", nullable = false)
    private String category;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;


}
