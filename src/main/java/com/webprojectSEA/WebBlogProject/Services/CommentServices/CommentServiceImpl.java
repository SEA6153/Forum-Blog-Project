package com.webprojectSEA.WebBlogProject.Services.CommentServices;

import com.webprojectSEA.WebBlogProject.Repostories.CommentRepository;
import com.webprojectSEA.WebBlogProject.model.PostComment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<PostComment> getCommentsByUserId(Long id) {
       return commentRepository.findAll();
    }
}
