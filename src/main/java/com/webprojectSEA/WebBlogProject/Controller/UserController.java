package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController{

    @Autowired
    private UserAccountRepository repository;

    @PostMapping("user/profile")
    @ModelAttribute
    public void commonUser(Principal p, Model m){
        if(p != null){
            String email = p.getName();
            Optional<UserAccount> user = repository.findByEmail(email);
            m.addAttribute("user", user);
        }
    }

    @GetMapping("user/profile")
    public String profile(){
        return "user_profile";
    }

}
