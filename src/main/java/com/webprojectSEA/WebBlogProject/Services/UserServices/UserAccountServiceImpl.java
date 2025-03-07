package com.webprojectSEA.WebBlogProject.Services.UserServices;

import com.webprojectSEA.WebBlogProject.Model.Roles;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public static final long LOCK_DURATION_TIME = 30000; // 30 seconds
    public static final long ATTEMPT_TIME = 3;

    public UserAccountServiceImpl(PasswordEncoder passwordEncoder, UserAccountRepository accountRepository, JavaMailSender javaMailSender) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public UserAccount getUserById(Long userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        userAccount.setRoles(Collections.singletonList(Roles.ROLE_USER)); // Assuming Roles.USER is the correct value
        // Kullanıcının mevcut enabled değerini koru, yeni kayıtlar için RegisterController'da false olarak ayarlanıyor
        // userAccount.setEnabled(true);
        return accountRepository.save(userAccount);
    }

    @Override
    public void disableUser(String username) {
        UserAccount userAccount = accountRepository.findByNickname(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        userAccount.setEnabled(false);
        accountRepository.save(userAccount);
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
        long lockTimeInMillis = userAccount.getLockTime().getTime();
        long currentTimeMillis = System.currentTimeMillis();
        if (lockTimeInMillis + LOCK_DURATION_TIME < currentTimeMillis) {
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
            userAccount.setRoles(Collections.singletonList(Roles.ROLE_USER));
        }
        return userAccount;
    }

    @Override
    public UserAccount getUserByUsername(String username) {
        return accountRepository.findByNickname(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Override
    public UserAccount findByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public void sendVerificationCode(UserAccount userAccount) {
        try {
            System.out.println("E-posta gönderme işlemi başlatılıyor...");
            System.out.println("E-posta adresi: " + userAccount.getEmail());
            System.out.println("Doğrulama kodu: " + userAccount.getVerificationCode());
            
            // JavaMailSender yapılandırmasını kontrol et
            System.out.println("JavaMailSender host: " + ((JavaMailSenderImpl)javaMailSender).getHost());
            System.out.println("JavaMailSender port: " + ((JavaMailSenderImpl)javaMailSender).getPort());
            System.out.println("JavaMailSender username: " + ((JavaMailSenderImpl)javaMailSender).getUsername());
            System.out.println("JavaMailSender password: " + (((JavaMailSenderImpl)javaMailSender).getPassword() != null ? "******" : "null"));
            
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(userAccount.getEmail());
            helper.setSubject("Forum Blog - E-posta Doğrulama Kodu");
            
            String content = 
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;'>" +
                "<h2 style='color: #333; text-align: center;'>E-posta Adresinizi Doğrulayın</h2>" +
                "<p style='color: #555; font-size: 16px;'>Merhaba " + userAccount.getFirstName() + ",</p>" +
                "<p style='color: #555; font-size: 16px;'>Forum Blog'a kaydolduğunuz için teşekkür ederiz. Hesabınızı aktifleştirmek için aşağıdaki doğrulama kodunu kullanın:</p>" +
                "<div style='background-color: #f5f5f5; padding: 15px; text-align: center; margin: 20px 0; border-radius: 4px;'>" +
                "<h1 style='color: #333; letter-spacing: 5px; font-size: 32px;'>" + userAccount.getVerificationCode() + "</h1>" +
                "</div>" +
                "<p style='color: #555; font-size: 16px;'>Bu kod 10 dakika süreyle geçerlidir.</p>" +
                "<p style='color: #555; font-size: 16px;'>Eğer bu kaydı siz yapmadıysanız, lütfen bu e-postayı dikkate almayın.</p>" +
                "<p style='color: #555; font-size: 16px; margin-top: 30px;'>Saygılarımızla,<br>Forum Blog Ekibi</p>" +
                "</div>";
            
            helper.setText(content, true);
            
            System.out.println("E-posta gönderiliyor: " + userAccount.getEmail() + " adresine doğrulama kodu: " + userAccount.getVerificationCode());
            javaMailSender.send(message);
            System.out.println("E-posta başarıyla gönderildi: " + userAccount.getEmail());
            
        } catch (Exception e) {
            System.err.println("E-posta gönderimi sırasında hata oluştu: " + e.getMessage());
            System.err.println("Hata detayı: ");
            e.printStackTrace();
            
            // Hata mesajını daha açıklayıcı hale getir
            String errorMessage = "E-posta gönderilirken bir hata oluştu: " + e.getMessage();
            
            if (e.getMessage() != null && e.getMessage().contains("Authentication failed")) {
                errorMessage += "\nGmail kimlik doğrulama hatası. Lütfen şunları kontrol edin:" +
                               "\n1. Gmail hesabınızda 2 adımlı doğrulama etkinleştirilmiş mi?" +
                               "\n2. Uygulama şifresini doğru oluşturdunuz mu?" +
                               "\n3. application.yml dosyasında doğru e-posta ve şifre girilmiş mi?";
            }
            
            throw new RuntimeException(errorMessage);
        }
    }

    @Override
    public boolean verify(String verificationCode) {
        Optional<UserAccount> userOptional = accountRepository.findByVerificationCode(verificationCode);
        
        if (userOptional.isEmpty()) {
            return false;
        }
        
        UserAccount user = userOptional.get();
        if (user.isEnabled()) {
            return false; 
        }
        
        user.setVerificationCode(null);
        user.setEnabled(true);
        accountRepository.save(user);
        return true;
    }
}
