package com.webprojectSEA.WebBlogProject.model;

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

    @Column
    @NotNull
    private Roles role;

    private boolean isEnabled;

    private boolean isAccountNonLocked;

    private int failedAttempt;

    private Date lockTime;

    private String verificationCode;

    @Column(nullable = false)
    private boolean active;

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
