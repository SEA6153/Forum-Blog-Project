package com.webprojectSEA.WebBlogProject.Services;

public interface LoginAttemptService {

    void loginSucced(String username);
    void loginFailed(String username);

    boolean isBlocked(String username);

    void handleFailedLoginAttempt(String nickname);
}
