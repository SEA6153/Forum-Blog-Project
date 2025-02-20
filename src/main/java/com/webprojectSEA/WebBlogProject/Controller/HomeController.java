package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Model.Category;
import com.webprojectSEA.WebBlogProject.Model.Post;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Services.AuthenticationService.AuthenticationServiceImpl;
import com.webprojectSEA.WebBlogProject.Services.PostService.PostServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {


    private final PostServiceImpl postServiceImpl;

    private final UserAccountRepository userAccountRepository;
    private final LoginController loginController;
    private final AuthenticationServiceImpl authenticationService;

    public HomeController(PostServiceImpl postServiceImpl, UserAccountRepository userAccountRepository, LoginController loginController, AuthenticationServiceImpl authenticationService){
        this.postServiceImpl = postServiceImpl;
        this.userAccountRepository = userAccountRepository;
        this.loginController = loginController;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        String loggedInUserEmail = authenticationService.getLoggedInUserNickname(authentication); // Kullanıcının e-postasını al
        if (loggedInUserEmail != null) {
            Optional<UserAccount> userAccount = userAccountRepository.findByEmail(loggedInUserEmail); // E-posta ile kullanıcıyı bul
            userAccount.ifPresent(user -> model.addAttribute("userAccount", user)); // Kullanıcıyı modele ekle
        }

        List<Post> post = postServiceImpl.getAll();
        model.addAttribute("post", post);
        model.addAttribute("categories", Category.values());
        return "form"; // Dönülecek Thymeleaf şablonu
    }
}
