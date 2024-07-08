package com.webprojectSEA.WebBlogProject.Services.PostService;

import com.webprojectSEA.WebBlogProject.Model.Category;
import com.webprojectSEA.WebBlogProject.Model.Post;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<Post> getById(Long id);
    Optional<Post> getByUserAccID(Long userID);
    List<Post> getAll();
    Post save(Post post, MultipartFile file) throws IOException;
    void deletePost(Long postID, Long loggedInUserId);
    public List<Post> getPostsByUserId(Long userId, String sortField, String sortDirection, String searchQuery, Category category);
    Post findById(Long id);
    UserAccount findByNickname(String nickname);
    void likePost(Long postId, String username);

    void dislikePost(Long postId, String username);

    void unlikePost(Long postId, String username);

    void undislikePost(Long postId, String username);


    List<Post> searchPosts(String query, String sortField, String sortDirection);

    String getTimeAgo(LocalDateTime createdAt);

    List<Post> getAllPosts(String sortField, String sortDirection, String searchQuery, Category category);

    Post seedDatasave(Post seedDataPost);
}
