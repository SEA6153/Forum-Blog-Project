
//DEFINE POSTING PROPERTIES

package com.webprojectSEA.WebBlogProject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private int likeCount;

    private int dislikeCount;

    private LocalDateTime createdAt;

    private LocalDateTime editDate;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        editDate = LocalDateTime.now();
    }

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount userAccount;

    @ManyToMany
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserAccount> likes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "post_dislikes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserAccount> dislikes = new HashSet<>();

    private String imageUrl;


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

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", category= '" +  category +"'" +
                ", title= '" + title + "'" +
                ", explanation= '" + explanation + "'" +
                ", createdAt= '" + createdAt + "'" +
                ", updatedAt= '" + updatedAt + "'" +
                ", likes = '" + likeCount + "'" +
                ", dislikes = '" + dislikeCount + "'" +
                ", editedAt = '" + editDate + "'" +
                ", postImg = '" + imageUrl + "'" +
                '}';
    }
}
