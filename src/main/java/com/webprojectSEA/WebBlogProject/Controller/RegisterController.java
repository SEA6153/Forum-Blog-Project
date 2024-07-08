package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.DTO.UserRegistrationDTO;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegisterController {

    private final UserAccountServiceImpl userAccountServiceImpl;

    public RegisterController(UserAccountServiceImpl userAccountServiceImpl) {
        this.userAccountServiceImpl = userAccountServiceImpl;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model){
        UserAccount account = new UserAccount();
        model.addAttribute("userAccount", account);
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid @ModelAttribute("userRegistrationDTO") UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Check if user already exists
        if (userAccountServiceImpl.findByUsernameOrEmail(userRegistrationDTO.getEmail()).isPresent()) {
            model.addAttribute("registrationError", "E-mail already registered.");
            return "register";
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setFirstName(userRegistrationDTO.getFirstName());
        System.out.println(userRegistrationDTO.getFirstName() + "++++++++++++++++++++++++++++++++++++++++++++++++");
        userAccount.setLastName(userRegistrationDTO.getLastName());
        userAccount.setEmail(userRegistrationDTO.getEmail());
        userAccount.setPassword(userRegistrationDTO.getPassword()); // Password will be encoded in service layer
        userAccount.setNickname(userRegistrationDTO.getNickname());

        userAccountServiceImpl.save(userAccount);
        return "redirect:/";
    }
}
