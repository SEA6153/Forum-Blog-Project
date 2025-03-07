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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final PostServiceImpl postServiceImpl;
    private final UserAccountRepository userAccountRepository;
    private final LoginController loginController;
    private final AuthenticationServiceImpl authenticationService;

    public HomeController(PostServiceImpl postServiceImpl, UserAccountRepository userAccountRepository, LoginController loginController, AuthenticationServiceImpl authenticationService) {
        this.postServiceImpl = postServiceImpl;
        this.userAccountRepository = userAccountRepository;
        this.loginController = loginController;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            // Kullanıcı bilgilerini ekle (eğer giriş yapılmışsa)
            if (authentication != null && authentication.isAuthenticated()) {
                String loggedInUserEmail = authenticationService.getLoggedInUserNickname(authentication);
                if (loggedInUserEmail != null) {
                    Optional<UserAccount> userAccount = userAccountRepository.findByEmail(loggedInUserEmail);
                    userAccount.ifPresent(user -> {
                        model.addAttribute("userAccount", user);
                        redirectAttributes.addFlashAttribute("userAccount", user);
                    });
                }
            }

            // Tüm gönderileri al
            List<Post> allPosts = postServiceImpl.getAll();

            if (allPosts != null && !allPosts.isEmpty()) {
                // En çok beğenilen gönderiler
                List<Post> mostLikedPosts = allPosts.stream()
                        .filter(post -> post.getLikeCount() > 0)
                        .sorted((p1, p2) -> Integer.compare(p2.getLikeCount(), p1.getLikeCount()))
                        .limit(5)
                        .collect(Collectors.toList());

                // En çok yorum alan gönderiler
                List<Post> mostCommentedPosts = allPosts.stream()
                        .filter(post -> post.getComments() != null && !post.getComments().isEmpty())
                        .sorted((p1, p2) -> Integer.compare(p2.getComments().size(), p1.getComments().size()))
                        .limit(5)
                        .collect(Collectors.toList());

                // Son eklenen gönderiler
                List<Post> recentPosts = allPosts.stream()
                        .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                        .limit(5)
                        .collect(Collectors.toList());

                // Model'e ve redirectAttributes'e attributeleri ekle
                model.addAttribute("mostLikedPosts", mostLikedPosts);
                model.addAttribute("mostCommentedPosts", mostCommentedPosts);
                model.addAttribute("recentPosts", recentPosts);
                
                redirectAttributes.addFlashAttribute("mostLikedPosts", mostLikedPosts);
                redirectAttributes.addFlashAttribute("mostCommentedPosts", mostCommentedPosts);
                redirectAttributes.addFlashAttribute("recentPosts", recentPosts);
            }

            model.addAttribute("categories", Category.values());
            redirectAttributes.addFlashAttribute("categories", Category.values());
            
            return "home";
            
        } catch (Exception e) {
            e.printStackTrace();
            // Boş listeler ekle
            model.addAttribute("mostLikedPosts", List.of());
            model.addAttribute("mostCommentedPosts", List.of());
            model.addAttribute("recentPosts", List.of());
            model.addAttribute("categories", Category.values());
            
            redirectAttributes.addFlashAttribute("mostLikedPosts", List.of());
            redirectAttributes.addFlashAttribute("mostCommentedPosts", List.of());
            redirectAttributes.addFlashAttribute("recentPosts", List.of());
            redirectAttributes.addFlashAttribute("categories", Category.values());
            
            return "home";
        }
    }
}
