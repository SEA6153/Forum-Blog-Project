package com.webprojectSEA.WebBlogProject.Repostories;

import com.webprojectSEA.WebBlogProject.model.Post;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import com.webprojectSEA.WebBlogProject.model.UserLikeDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLikeDislikeRepository extends JpaRepository<UserLikeDislike, Long> {
    Optional<UserLikeDislike> findByUserAndPost(Optional<UserAccount> user, Post post);
}