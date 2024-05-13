package com.webprojectSEA.WebBlogProject.Services.UserServices;

import com.webprojectSEA.WebBlogProject.model.UserAccount;

import java.util.List;

public interface UserAccountService {


    UserAccount save(UserAccount userAccount);
    void disableUser(String username);
    List<UserAccount> getAllActiveUserAccount(boolean isActive);
    public void increaseFailedAttempt(UserAccount userAccount);
    public void resetAttempt(String nickname);
    public void lock(UserAccount userAccount);
    public boolean unlockAccountTimeExpired(UserAccount userAccount);





}
