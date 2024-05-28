package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserAuthorityRepository;
import com.webprojectSEA.WebBlogProject.Services.FileServices.FileStorageService;
import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountService;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController {

private final UserAccountRepository userAccountRepository;
private final UserAuthorityRepository userAuthorityRepository;
private final FileStorageService fileStorageService;
private final UserAccountService userAccountService;

    public UserController(UserAccountRepository userAccountRepository, UserAuthorityRepository userAuthorityRepository, FileStorageService fileStorageService, UserAccountService userAccountService) {
        this.userAccountRepository = userAccountRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.fileStorageService = fileStorageService;
        this.userAccountService = userAccountService;
    }

    @GetMapping("/user/{nickname}/profile")
    public String commonUser(Model m, @PathVariable String nickname) {
        Optional<UserAccount> user = userAccountRepository.findByNickname(nickname);
        if (user.isPresent()) {
            UserAccount userAccount = user.get();
            m.addAttribute("userAccount", userAccount);
            return "user_profile";
        } else {
            return "404";
        }
    }


    @PostMapping("/user/{nickname}/profile")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Principal principal, @PathVariable String nickname) {
        UserAccount userAccount = userAccountRepository.findByNickname(principal.getName()) .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + nickname));
        fileStorageService.store(file);
        userAccount.setUserProfilePic(file.getOriginalFilename());
        userAccountRepository.save(userAccount);
        return "redirect:/user/{nickname}/profile";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public Resource serveFile(@PathVariable String filename) throws MalformedURLException {
        Path file = fileStorageService.load(filename);
        Resource resource = new UrlResource(file.toUri());
        return resource;
    }


}

