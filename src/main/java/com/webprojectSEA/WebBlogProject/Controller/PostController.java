package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.CommentRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Services.PostService.PostServiceImpl;
import com.webprojectSEA.WebBlogProject.model.Post;
import com.webprojectSEA.WebBlogProject.model.PostComment;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

    private final PostServiceImpl postServiceImpl;
    private final UserAccountRepository userAccountRepository;
    private final LoginController loginController;
    private final CommentRepository commentRepository;

    public PostController(PostServiceImpl postServiceImpl, UserAccountRepository userAccountRepository, LoginController loginController, CommentRepository commentRepository) {
        this.postServiceImpl = postServiceImpl;
        this.userAccountRepository = userAccountRepository;
        this.loginController = loginController;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Post> optionalPost = postServiceImpl.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<PostComment> postComments = commentRepository.findByPost(post);

            boolean isPostOwner = authentication != null && authentication.isAuthenticated() && post.getUserAccount().getNickname().equals(authentication.getName());
            model.addAttribute("isPostOwner", isPostOwner);

            if (authentication != null && authentication.isAuthenticated()) {
                String currentUsername = authentication.getName();
                UserAccount userAccount = userAccountRepository.findByNickname(currentUsername).orElse(null);
                model.addAttribute("userAccount", userAccount);
            }

            model.addAttribute("post", post);
            model.addAttribute("postComments", postComments);
            return "post";
        } else {
            return "404";
        }
    }

    @GetMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String createNewPost(Model model, Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findById(loggedInUserId);
        if (optionalUserAccount.isPresent()) {
            Post post = new Post();
            post.setUserAccount(optionalUserAccount.get());
            model.addAttribute("post", post);
            return "post_new";
        } else {
            return "404";
        }
    }

    @PostMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String saveNewPost(@ModelAttribute Post post, Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findById(loggedInUserId);
        if (optionalUserAccount.isPresent()) {
            post.setUserAccount(optionalUserAccount.get());
            postServiceImpl.save(post);
            return "redirect:/posts/" + post.getId();
        } else {
            return "404";
        }
    }

    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String getPostForEdit(@PathVariable Long id, Model model, Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        Optional<Post> optionalPost = postServiceImpl.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (post.getUserAccount().getId().equals(loggedInUserId)) {
                model.addAttribute("post", post);
                return "post_edit";
            } else {
                return "403";
            }
        } else {
            return "404";
        }
    }

    @PostMapping("/posts/{id}/update")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@PathVariable Long id, Post post, Authentication authentication){
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        Optional<Post> optionalPost = postServiceImpl.getById(id);
        if(optionalPost.isPresent()){
            Post existingPost = optionalPost.get();
            if(existingPost.getUserAccount().getId().equals(loggedInUserId)){
                existingPost.setTitle(post.getTitle());
                existingPost.setExplanation(post.getExplanation());
                existingPost.setCategory(post.getCategory());
                postServiceImpl.save(existingPost);
                return "redirect:/posts/" + id;
            } else {
                return "403";
            }
        } else {
            return "404";
        }
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, Authentication authentication) {
        Long loggedInUserId = Long.valueOf(loginController.getLoggedInUserId(authentication));

        Optional<Post> optionalPost = postServiceImpl.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            if (post.getUserAccount().getId().equals(loggedInUserId)) {
                postServiceImpl.deletePost(id, loggedInUserId);
                return "redirect:/";
            } else {
                return "403";
            }
        } else {
            return "404";
        }
    }
    @GetMapping("/my/posts")
    @PreAuthorize("isAuthenticated()")
    public String myPosts(Model model, Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        List<Post> userPosts = postServiceImpl.getPostsByUserId(loggedInUserId);

        model.addAttribute("userPosts", userPosts);
        return "my_posts";
    }
}
