
//TESTING THE POST SERVICE

package com.webprojectSEA.WebBlogProject.Configuration;

import com.webprojectSEA.WebBlogProject.Services.PostService;
import com.webprojectSEA.WebBlogProject.Services.UserAccountService;
import com.webprojectSEA.WebBlogProject.model.Category;
import com.webprojectSEA.WebBlogProject.model.Post;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeedData implements CommandLineRunner {

    private final PostService postService;
    private final UserAccountService accountService;
    public SeedData(PostService postService, UserAccountService accountService) {
        this.postService = postService;
        this.accountService = accountService;
    }






    @Override
    public void run(String... args) throws Exception{
        List<Post> posts = postService.getAll();

        if(posts.isEmpty()){

            UserAccount account1 = new UserAccount();
            UserAccount account2 = new UserAccount();
            UserAccount account3 = new UserAccount();


            account1.setFirstName("Samet1 Ege1");
            account1.setLastName("AŞIK1");
            account1.setEmail("sametrize1@hotmail.com");
            account1.setPassword("61536153");
            account1.setNickname("SEA6153-1");

            account2.setFirstName("Samet2 Ege2");
            account2.setLastName("AŞIK2");
            account2.setEmail("sametrize2@hotmail.com");
            account2.setPassword("61536153-2");
            account2.setNickname("SEA6153-2");


            account3.setFirstName("Samet3 Ege3");
            account3.setLastName("AŞIK3");
            account3.setEmail("sametrize3@hotmail.com");
            account3.setPassword("61536153-3");
            account3.setNickname("SEA6153-3");



            accountService.save(account1);
            accountService.save(account2);
            accountService.save(account3);


            Post post1 = new Post();
            post1.setTitle("Title of post1");
            post1.setExplanation("Explanation of post1");
            post1.setCategory(Category.GAMES);
            post1.setUserAccount(account1);


            Post post2 = new Post();
            post2.setTitle("Title of post2");
            post2.setExplanation("Explanation of post2");
            post2.setCategory(Category.BOOKS);
            post2.setUserAccount(account2);

            Post post3 = new Post();
            post3.setCategory(Category.SPORT);
            post3.setExplanation("Explanation of post3");
            post3.setTitle("Title of post3");
            post3.setUserAccount(account3);

            postService.save(post1);
            postService.save(post2);
            postService.save(post3);











        }


    }

}
