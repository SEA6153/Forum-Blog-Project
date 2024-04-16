
package com.webprojectSEA.WebBlogProject.Services;


import com.webprojectSEA.WebBlogProject.Repostories.PostRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.model.Post;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;

    public PostServiceImpl(PostRepository postRepository, UserAccountRepository userAccountRepository) {
        this.postRepository = postRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public Optional<Post> getById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Optional<Post> getByUserAccID(Long userID){
        return postRepository.findByUserAccountId(userID);    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postID, Long loggedInUserId) {
        Optional<Post> postOptional = postRepository.findById(postID);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            postRepository.delete(post);
        } else {
            throw new IllegalArgumentException("Post not found");
        }
    }
}

