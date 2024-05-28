package com.webprojectSEA.WebBlogProject.Repostories;

import com.webprojectSEA.WebBlogProject.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthorityRepository extends JpaRepository<Authority, Long> {

}
