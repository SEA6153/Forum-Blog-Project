
//DEFINE POSTING PROPERTIES

package com.webprojectSEA.WebBlogProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

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
    @JoinColumn(name = "user_account_id" ,referencedColumnName = "id", nullable = false)
    private UserAccount userAccount;


}
