package com.webprojectSEA.WebBlogProject.Repostories;

import com.webprojectSEA.WebBlogProject.Model.Category;
import com.webprojectSEA.WebBlogProject.Model.Post;
import com.webprojectSEA.WebBlogProject.Model.PostComment;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<PostComment, Long> {

    List<PostComment> findByPost(Post post);
    List<PostComment> findByUserAccountId(Long userAccountId);
    List<PostComment> findByUserAccountNickname(String nickname);
    List<PostComment> findByUserAccountAndTextContaining(UserAccount userAccount, String text, Sort sort);

    List<PostComment> findByUserAccountAndTextContainingAndPostCategory(UserAccount userAccount, String text, Category category, Sort sort);
}
