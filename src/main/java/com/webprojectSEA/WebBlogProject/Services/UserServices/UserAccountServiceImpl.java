package com.webprojectSEA.WebBlogProject.Services.UserServices;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.model.Roles;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserAccountServiceImpl implements UserAccountService{


    private final PasswordEncoder passwordEncoder;

    private final  UserAccountRepository accountRepository;

    public static final long LOCK_DURATION_TIME = 30000;
    public static final long ATTEMPT_TIME = 3;

    public UserAccountServiceImpl(PasswordEncoder passwordEncoder, UserAccountRepository accountRepository) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }
    @Override
    public UserAccount getUserById(Long userId) {
        return accountRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        userAccount.setRoles(Collections.singletonList(Roles.ROLE_USER));
        userAccount.setEnabled(true);

        return accountRepository.save(userAccount);
    }

    @Override
    public void disableUser(String username) {
        Optional<UserAccount> optionalUserAccount = accountRepository.findByNickname(username);
        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();
            userAccount.setEnabled(false);
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
        if(lockTimeInMills + LOCK_DURATION_TIME < currentTimeMillis){
            userAccount.setAccountNonLocked(true);
            userAccount.setLockTime(null);
            userAccount.setFailedAttempt(0);
            accountRepository.save(userAccount);
            return true;
        }
        return false;
    }

    @Override
    public Optional<UserAccount> findByUsernameOrEmail(String identifier) {
        Optional<UserAccount> userAccount = accountRepository.findByNickname(identifier);
        if (userAccount.isEmpty()) {
            userAccount = accountRepository.findByEmail(identifier);
        }
        return userAccount;
    }
    @Override
    public UserAccount ensureRoles(UserAccount userAccount) {
        if (userAccount.getRoles() == null || userAccount.getRoles().isEmpty()) {
            userAccount.setRoles(Arrays.asList(Roles.valueOf("USER")));
        }
        return userAccount;
    }

    @Override
    public UserAccount getUserByUsername(String username) {
        return accountRepository.findByNickname(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    }


