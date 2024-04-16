package com.webprojectSEA.WebBlogProject.Services;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;

    private Map<String, Integer> attemptsCache = new HashMap<>();

    private final UserAccountServiceImpl userAccountServiceImpl;
    private final UserAccountRepository accountRepository;

    // Constructor Injection
    @Autowired
    public LoginAttemptServiceImpl(UserAccountServiceImpl userAccountServiceImpl, UserAccountRepository accountRepository) {
        this.userAccountServiceImpl = userAccountServiceImpl;
        this.accountRepository = accountRepository;
    }

    @Override
    public void loginSucced(String username) {
        attemptsCache.remove(username);
    }

    @Override
    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);
        if (attempts >= MAX_ATTEMPTS) {
            userAccountServiceImpl.disableUser(username);
            attemptsCache.remove(username);
        } else {
            // Başarısız giriş denemesi olduğunda failedAttempt değerini artır
            handleFailedLoginAttempt(username);
        }
    }

    @Override
    public boolean isBlocked(String username) {
        return attemptsCache.containsKey(username) && attemptsCache.get(username) >= MAX_ATTEMPTS;
    }

    @Override
    public void handleFailedLoginAttempt(String nickname) {
        Optional<UserAccount> userAccount = accountRepository.findByNickname(nickname);

        if (userAccount.isPresent()) {
            UserAccount user = userAccount.get();
            int failedAttempt = user.getFailedAttempt();
            failedAttempt++;
            if (failedAttempt >= MAX_ATTEMPTS) {
                user.setAccountNonLocked(false);
                accountRepository.save(user);
                throw new LockedException("Your account is locked due to too many failed login attempts.");
            } else {
                user.setFailedAttempt(failedAttempt);
                accountRepository.save(user);
            }
        }
    }
}
