package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserAuthorityRepository;
import com.webprojectSEA.WebBlogProject.Services.AWSServices.AwsS3Service;
import com.webprojectSEA.WebBlogProject.Services.PostService.PostServiceImpl;
import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountService;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserAccountRepository userAccountRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final AwsS3Service awsS3Service;
    private final UserAccountService userAccountService;
    private final LoginController loginController;
    private final PostServiceImpl postServiceImpl;

    public UserController(UserAccountRepository userAccountRepository, UserAuthorityRepository userAuthorityRepository, AwsS3Service awsS3Service, UserAccountService userAccountService, LoginController loginController, PostServiceImpl postServiceImpl) {
        this.userAccountRepository = userAccountRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.awsS3Service = awsS3Service;
        this.userAccountService = userAccountService;
        this.loginController = loginController;
        this.postServiceImpl = postServiceImpl;
    }

    @GetMapping("/user/{nickname}/profile")
    public String userProfile(@PathVariable String nickname, Model model, Authentication authentication) {
        UserAccount loggedInUser = loginController.getLoggedInUser(authentication);
        UserAccount userAccount = postServiceImpl.findByNickname(nickname);

        model.addAttribute("userAccount", userAccount);

        if (loggedInUser != null && loggedInUser.getNickname().equals(nickname)) {
            model.addAttribute("isOwnProfile", true);
        } else {
            model.addAttribute("isOwnProfile", false);
        }

        return "user_profile";
    }

    @PostMapping("/user/{nickname}/profile")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @PathVariable String nickname,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        String currentEmail = userDetails.getUsername();
        UserAccount userAccount = userAccountRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("User not found: " + nickname));
        if (!userAccount.getEmail().equals(currentEmail)) {
            redirectAttributes.addFlashAttribute("error", "You cannot change another user's profile.");
            return "redirect:/user/" + nickname + "/profile";
        }

        try {
            String imageUrl = awsS3Service.uploadFile(file, "profilePics");
            userAccount.setUserProfilePic(imageUrl);
            userAccountRepository.save(userAccount);
            redirectAttributes.addFlashAttribute("message", "Profile picture updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Profile picture update failed: " + e.getMessage());
        }

        return "redirect:/user/" + nickname + "/profile";
    }

}

