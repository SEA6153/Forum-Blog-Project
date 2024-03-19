package com.webprojectSEA.WebBlogProject.Repostories;

import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository  extends JpaRepository<UserAccount, Long> {


    Optional<UserAccount> findByEmail(String email);
}
