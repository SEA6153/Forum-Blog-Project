
//DEFINE POST SERVICE PROPERTIES AS SAVE, FIND, GET ALL..


package com.webprojectSEA.WebBlogProject.Services;


import com.webprojectSEA.WebBlogProject.Repostories.PostRepository;
import com.webprojectSEA.WebBlogProject.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Optional<Post> getById(Long id){
        return postRepository.findById(id);
    }


    public List<Post> getAll(){
        return postRepository.findAll();
    }

    public Post save(Post post){
        if(post.getId() == null){
            post.setCreatedAt(LocalDateTime.now());
        }
        return  postRepository.save(post);
    }

}
