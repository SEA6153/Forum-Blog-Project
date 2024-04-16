package com.webprojectSEA.WebBlogProject.Services;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserAuthorityRepository;
import com.webprojectSEA.WebBlogProject.model.Authority;
import com.webprojectSEA.WebBlogProject.model.Roles;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserAccountServiceImpl implements UserAccountService{

    private Roles roles;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository accountRepository;
    private  final UserAuthorityRepository authorityRepository;

    private static final long lock_duration_time = 30000;

    // Constructor Injection
    public UserAccountServiceImpl(PasswordEncoder passwordEncoder, UserAccountRepository accountRepository, UserAuthorityRepository authorityRepository) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public UserAccount save(UserAccount userAccount) {

        userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));


        Authority userAuthority = authorityRepository.findByName("USER");
        if (userAuthority == null) {
            userAuthority = new Authority();
            userAuthority.setName(Roles.USER.toString());
            userAuthority = authorityRepository.save(userAuthority);
        }
        Set<Authority> authorities = new HashSet<>();
        authorities.add(userAuthority);
        userAccount.setAuthoritySet(authorities);


        userAccount.setActive(true);

        return accountRepository.save(userAccount);
    }

    @Override
    public void disableUser(String username) {
        Optional<UserAccount> optionalUserAccount = accountRepository.findByNickname(username);
        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();
            userAccount.setActive(false);
            accountRepository.save(userAccount);
        } else {
            throw new RuntimeException("User not found with username: " + username);
        }
    }


    @Override
    public List<UserAccount> getAllActiveUserAccount(boolean isActive) {
        List<UserAccount> activeUsers = accountRepository.findByActive(isActive);
        if (activeUsers.isEmpty()) {
            throw new NoSuchElementException("No active users found");
        }
        return activeUsers;
    }

    @Override
    public void increaseFailedAttempt(UserAccount userAccount) {
      int attempt = userAccount.getFailedAttempt() + 1;
      accountRepository.updateFailedAttempt(attempt, userAccount.getNickname());
    }

    @Override
    public void resetAttempt(String nickname) {
    accountRepository.updateFailedAttempt(0, nickname);
    }

    @Override
    public void lock(UserAccount userAccount) {
        userAccount.setAccountNonLocked(false);
        userAccount.setLockTime(new Date());
        accountRepository.save(userAccount);
    }

    @Override
    public boolean unlockAccountTimeExpired(UserAccount userAccount) {
        long lockTimeInMills = userAccount.getLockTime().getTime();
        long currentTimeMillis = System.currentTimeMillis();
        if(lockTimeInMills + lock_duration_time < currentTimeMillis){
            userAccount.setAccountNonLocked(true);
            userAccount.setLockTime(null);
            userAccount.setFailedAttempt(0);
            accountRepository.save(userAccount);
            return true;
        }
        return false;
    }
}
