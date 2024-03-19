package com.webprojectSEA.WebBlogProject.Services;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountService {

    private final UserAccountRepository accountRepository;

    // Constructor Injection
    public UserAccountService(UserAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public UserAccount save(UserAccount userAccount){

        return accountRepository.save(userAccount);

    }


    public Optional<UserAccount> findByEmail(String email) {
        return accountRepository.findByEmail(email);

    }
}
