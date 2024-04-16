package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Services.UserAccountServiceImpl;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserControllerImpl{
    private final UserAccountServiceImpl userAccountServiceImpl;
    private final UserAccountRepository userAccountRepository;

    public UserControllerImpl(UserAccountServiceImpl userAccountServiceImpl, UserAccountRepository userAccountRepository) {
        this.userAccountServiceImpl = userAccountServiceImpl;
        this.userAccountRepository = userAccountRepository;
    }



    public List<UserAccount> getActiveUsers(boolean isActive) {
        // Aktif durumdaki tüm kullanıcıları al
        return userAccountServiceImpl.getAllActiveUserAccount(isActive);
    }


}
