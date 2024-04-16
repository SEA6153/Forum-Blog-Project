package com.webprojectSEA.WebBlogProject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 30)
    private String firstName;

    @Column(nullable = false, length = 30)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 12, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    private boolean isEnabled;

    private boolean isAccountNonLocked;

    private int failedAttempt;

    private Date lockTime;

    private String verificationCode;

    @OneToMany(mappedBy = "userAccount")
    private List<Post> posts;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_account_authority", joinColumns = {@JoinColumn(name = "user_account_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    private Set<Authority> authoritySet = new HashSet<>();

    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", posts=" + posts +
                ", authoritySet=" + authoritySet +
                ", enabled= " + isEnabled +
                '}';
    }

}
