
//POST REPO

package com.webprojectSEA.WebBlogProject.Repostories;



import com.webprojectSEA.WebBlogProject.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {


}
