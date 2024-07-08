package com.webprojectSEA.WebBlogProject.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Length(max = 30)
    private String firstName;

    @Length(max = 30)
    @NotNull
    private String lastName;

    @NotNull
    private String password;

    @Column(unique = true)
    @Length(max = 15)
    @NotNull
    private String nickname;

    @Column(unique = true)
    @NotNull
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Roles> roles;

    private boolean isEnabled;

    private boolean isAccountNonLocked;

    private int failedAttempt;

    private Date lockTime;

    private String verificationCode;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "userAccount")
    private List<Post> posts;

    private String userProfilePic;
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments;

    @ManyToMany(mappedBy = "likes")
    private Set<Post> likedPosts = new HashSet<>();

    @ManyToMany(mappedBy = "dislikes")
    private Set<Post> dislikedPosts = new HashSet<>();



    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", role=" + roles +
                ", isEnabled=" + isEnabled +
                ", isAccountNonLocked=" + isAccountNonLocked +
                ", failedAttempt=" + failedAttempt +
                ", lockTime=" + lockTime +
                ", verificationCode='" + verificationCode + '\'' +
                ", active=" + active +
                ", posts=" + posts +
                ", userProfilePic=" + userProfilePic +
                ", comments= " + comments +
                ", likedPosts = " + likedPosts +
                "; dislikedPosts = " + dislikedPosts +
                '}';
    }
}
