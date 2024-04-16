package com.webprojectSEA.WebBlogProject.Configuration;

import com.webprojectSEA.WebBlogProject.Services.LoginAttemptServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    private final LoginAttemptServiceImpl loginAttemptServiceImpl;

    public AuthenticationFailureHandler(LoginAttemptServiceImpl loginAttemptServiceImpl) {
        this.loginAttemptServiceImpl = loginAttemptServiceImpl;
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String nickname = request.getParameter("nickname"); //

        // Başarısız giriş denemesini işleyin ve kullanıcı hesabını güncelleyin
            loginAttemptServiceImpl.handleFailedLoginAttempt(nickname);

        // Diğer işlemler veya yönlendirmeler yapılabilir
        response.sendRedirect("/login?error"); // Örneğin, hata mesajıyla birlikte giriş sayfasına yönlendirme



    }
}
