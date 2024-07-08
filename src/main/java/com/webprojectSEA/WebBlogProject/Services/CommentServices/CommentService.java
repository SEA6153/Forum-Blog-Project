package com.webprojectSEA.WebBlogProject.Services.CommentServices;

import com.webprojectSEA.WebBlogProject.Model.Category;
import com.webprojectSEA.WebBlogProject.Model.PostComment;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {



    List<PostComment> getCommentsByUserId(Long userId, String sortField, String sortDirection, String query, Category category);

    PostComment getCommentById(Long id);

    public List<PostComment> findCommentsByUsername(String username);


    void addComment(Long postId, String username, String text, String photoUrl, String email, Long userId);

    void updateComment(Long id, String text, String photoUrl);

    void deleteComment(Long id);

    @Transactional
    void likeComment(Long commentId, String username);

    @Transactional
    void dislikeComment(Long commentId, String username);

    @Transactional
    void unlikeComment(Long commentId, String username);

    @Transactional
    void undislikeComment(Long commentId, String username);

    String getTimeAgoComment(LocalDateTime createdAt);
}
