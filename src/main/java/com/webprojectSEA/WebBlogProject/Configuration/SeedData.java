
//TESTING THE POST SERVICE

package com.webprojectSEA.WebBlogProject.Configuration;

import com.webprojectSEA.WebBlogProject.Services.PostService;
import com.webprojectSEA.WebBlogProject.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    private PostService postService;

    @Override
    public void run(String... args) throws Exception{
        List<Post> posts = postService.getAll();

        if(posts.isEmpty()){
            Post post1 = new Post();
            post1.setTitle("Title of post1");
            post1.setExplanation("Explanation of post1");
            post1.setCategory("Category of post1");


            Post post2 = new Post();
            post2.setTitle("Title of post2");
            post2.setExplanation("Explanation of post2");
            post2.setCategory("Category of post2");

            Post post3 = new Post();
            post3.setCategory("Category of post3");
            post3.setExplanation("Explanation of post3");
            post3.setTitle("Title of post3");

            postService.save(post1);
            postService.save(post2);
            postService.save(post3);


        }


    }

}
