package com.webprojectSEA.WebBlogProject.Services.UserServices;

import com.webprojectSEA.WebBlogProject.model.UserAccount;

import java.util.List;
import java.util.Optional;

public interface UserAccountService {


    UserAccount getUserById(Long userId);

    UserAccount save(UserAccount userAccount);
    void disableUser(String username);
    List<UserAccount> getAllActiveUserAccount(boolean isActive);
    public void increaseFailedAttempt(UserAccount userAccount);
    public void resetAttempt(String nickname);
    public void lock(UserAccount userAccount);
    public boolean unlockAccountTimeExpired(UserAccount userAccount);
    public Optional<UserAccount> findByUsernameOrEmail(String identifier);
    public UserAccount ensureRoles(UserAccount userAccount);

    UserAccount getUserByUsername(String username);
}
