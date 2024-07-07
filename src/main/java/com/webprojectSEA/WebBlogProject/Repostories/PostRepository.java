
//POST REPO

package com.webprojectSEA.WebBlogProject.Repostories;


import com.webprojectSEA.WebBlogProject.model.Category;
import com.webprojectSEA.WebBlogProject.model.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUserAccountId(Long userID);
    List<Post> findByTitleContaining(String title, Sort sort);
    List<Post> findByTitleContainingIgnoreCase(String title);
    List<Post> findByCategory(Category category, Sort sort);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:title% AND p.category = :category")
    List<Post> findByTitleContainingAndCategory(@Param("title") String title, @Param("category") Category category, Sort sort);

    List<Post> findAll(Specification<Post> spec, Sort sort);
}
