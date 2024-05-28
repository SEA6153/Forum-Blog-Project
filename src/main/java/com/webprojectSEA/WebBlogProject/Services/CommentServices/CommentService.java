package com.webprojectSEA.WebBlogProject.Services.CommentServices;

import com.webprojectSEA.WebBlogProject.model.PostComment;

import java.util.List;

public interface CommentService {

    List<PostComment> getCommentsByUserId(Long id);


}
