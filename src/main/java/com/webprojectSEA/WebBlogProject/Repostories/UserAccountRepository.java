package com.webprojectSEA.WebBlogProject.Repostories;

import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository  extends JpaRepository<UserAccount, Long> {


    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByNickname(String nickname);
    List<UserAccount> findByActive(boolean isEnabled);

    @Query("update UserAccount u set u.failedAttempt=?1 where u.nickname=?2")
    @Modifying
    void updateFailedAttempt(int attempt, String nickname);


    default Optional<UserAccount> findByNicknameOrEmail(String nickname, String email) {
        Optional<UserAccount> userAccount = findByNickname(nickname);
        if (!userAccount.isPresent()) {
            userAccount = findByEmail(email);
        }
        return userAccount;
    }
}
