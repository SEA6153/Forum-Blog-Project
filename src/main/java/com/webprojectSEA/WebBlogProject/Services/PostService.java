package com.webprojectSEA.WebBlogProject.Services;

import com.webprojectSEA.WebBlogProject.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<Post> getById(Long id);
    Optional<Post> getByUserAccID(Long userID);
    List<Post> getAll();
    Post save(Post post);
    void deletePost(Long postID, Long loggedInUserId);

}
