package com.webprojectSEA.WebBlogProject.Repostories;

import com.webprojectSEA.WebBlogProject.Model.Post;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import com.webprojectSEA.WebBlogProject.Model.UserLikeDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLikeDislikeRepository extends JpaRepository<UserLikeDislike, Long> {
    Optional<UserLikeDislike> findByUserAndPost(Optional<UserAccount> user, Post post);
}