package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Services.PostService.PostServiceImpl;
import com.webprojectSEA.WebBlogProject.model.Category;
import com.webprojectSEA.WebBlogProject.model.Post;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private PostServiceImpl postServiceImpl;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private LoginController loginController;

    public HomeController(PostServiceImpl postServiceImpl){
        this.postServiceImpl = postServiceImpl;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        String loggedInUserNicknameOrEmail = loginController.getLoggedInUserNickname(authentication);
        if (loggedInUserNicknameOrEmail != null) {
            Optional<UserAccount> userAccount = userAccountRepository.findByNicknameOrEmail(loggedInUserNicknameOrEmail, loggedInUserNicknameOrEmail);
            userAccount.ifPresent(user -> model.addAttribute("userAccount", user));
        }

        List<Post> post = postServiceImpl.getAll();
        model.addAttribute("post", post);
        model.addAttribute("categories", Category.values());
        return "form";
    }
}
