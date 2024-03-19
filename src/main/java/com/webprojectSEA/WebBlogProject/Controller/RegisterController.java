package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Services.UserAccountService;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final UserAccountService userAccountService;

    public RegisterController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model){
        UserAccount account = new UserAccount();
        model.addAttribute("userAccount", account);
        return "register";
    }

    @PostMapping("/register")
    public String registerNewUser(@ModelAttribute UserAccount userAccount){
        userAccountService.save(userAccount);
        return "redirect:/";
    }


}
