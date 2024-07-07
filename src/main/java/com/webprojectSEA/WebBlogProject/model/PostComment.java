package com.webprojectSEA.WebBlogProject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String text;

    private int likeCount;

    private int dislikeCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userAccount_id", nullable = false)
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Transient
    private String postTitle;

    private LocalDateTime createdAt;
    private String photoUrl;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    @ManyToMany
    @JoinTable(
            name = "comment_likes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserAccount> likes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "comment_dislikes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserAccount> dislikes = new HashSet<>();
    @Transient
    public String getTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);
        long days = duration.toDays();
        if (days > 0) {
            return days == 1 ? "1 day ago" : days + " days ago";
        }
        long hours = duration.toHours();
        if (hours > 0) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        }
        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        }
        return "just now";
    }
}
