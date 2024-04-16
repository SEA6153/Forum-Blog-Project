package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Services.PostServiceImpl;
import com.webprojectSEA.WebBlogProject.model.Post;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PostController {


    private final PostServiceImpl postServiceImpl;

    private final UserAccountRepository userAccountRepository;
    private final LoginController loginController;

    public PostController(PostServiceImpl postServiceImpl, UserAccountRepository userAccountRepository, LoginController loginController) {
        this.postServiceImpl = postServiceImpl;
        this.userAccountRepository = userAccountRepository;
        this.loginController = loginController;
    }


    //Find the post by id
    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id, Model model){
        // If the post exists, then shove it into the model.
        Optional<Post> optionalPost = postServiceImpl.getById(id);
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
    @PreAuthorize("isAuthenticated()")
    public String createNewPost(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findByEmail(email);
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
        postServiceImpl.save(post);
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String getPostForEdit(@PathVariable Long id, Model model){

        Optional<Post> optionalPost = postServiceImpl.getById(id);
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            model.addAttribute("post", post);
            return "post_edit";
        }
        else {
            return "404";
        }
    }
    @PostMapping("/posts/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@PathVariable Long id, Post post){
        Optional<Post> optionalPost = postServiceImpl.getById(id);
        if(optionalPost.isPresent()){
            Post existingPost = optionalPost.get();

            existingPost.setTitle(post.getTitle());
            existingPost.setExplanation(post.getExplanation());
            existingPost.setCategory(post.getCategory());

            postServiceImpl.save(existingPost);
        }
        return "redirect:/posts/" + post.getId();
    }

    //@PostMapping("/posts/{id}/delete")
    @DeleteMapping("{id}")
    public String deletePost(@PathVariable Long id, Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);

        Optional<Post> optionalPost = postServiceImpl.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Postun sahibi ile oturum açmış kullanıcıyı karşılaştır
            if (post.getUserAccount().getId().equals(loggedInUserId)) {
                postServiceImpl.deletePost(id, loggedInUserId);
                return "redirect:/";
            } else {
                // Yetkisiz kullanıcı
                return "403"; // Erişim engellendi sayfasına yönlendir
            }
        } else {
            // Post bulunamadı
            return "404"; // Sayfa bulunamadı sayfasına yönlendir
        }
    }




}
