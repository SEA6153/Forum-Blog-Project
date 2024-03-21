package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Services.PostService;
import com.webprojectSEA.WebBlogProject.Services.UserAccountService;
import com.webprojectSEA.WebBlogProject.model.Category;
import com.webprojectSEA.WebBlogProject.model.Post;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class PostController {


    private final PostService postService;
    private final UserAccountService userAccountService;

    public PostController(PostService postService, UserAccountService userAccountService) {
        this.postService = postService;
        this.userAccountService = userAccountService;
    }


    //Find the post by id
    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id, Model model){
        // If the post exists, then shove it into the model.
        Optional<Post> optionalPost = postService.getById(id);
        if(optionalPost.isPresent()){
        Post post = optionalPost.get();
        model.addAttribute("post", post);
        return "post";
        }
    else {
        return "404";
        }
    }

    @GetMapping("/posts/new")
    public String createNewPost(Model model){
        Optional<UserAccount> optionalUserAccount = userAccountService.findByEmail("sametrize1@hotmail.com");
        if (optionalUserAccount.isPresent()){

            Post post = new Post();
            post.setUserAccount(optionalUserAccount.get());
            model.addAttribute(post);
            return "post_new";

        }
        else{
            return "404";
        }
    }

    @PostMapping("/posts/new")
    public String saveNewPost(@ModelAttribute Post post) {
        postService.save(post);
        return "redirect:/posts/" + post.getId();
    }

}
