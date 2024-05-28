
//TESTING THE POST SERVICE

package com.webprojectSEA.WebBlogProject.Configuration;

import com.webprojectSEA.WebBlogProject.Repostories.UserAuthorityRepository;
import com.webprojectSEA.WebBlogProject.Services.PostService.PostServiceImpl;
import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountServiceImpl;
import com.webprojectSEA.WebBlogProject.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SeedData implements CommandLineRunner {
    private Roles roles;

    private final PostServiceImpl postServiceImpl;
    private final UserAccountServiceImpl accountService;

private final UserAuthorityRepository userAuthorityRepository;



    public SeedData(PostServiceImpl postServiceImpl, UserAccountServiceImpl accountService, UserAuthorityRepository userAuthorityRepository) {
        this.postServiceImpl = postServiceImpl;
        this.accountService = accountService;
        this.userAuthorityRepository = userAuthorityRepository;
    }






    @Override
    public void run(String... args) throws Exception{
        List<Post> posts = postServiceImpl.getAll();


        if(posts.isEmpty()){


            Authority user = new Authority();
            user.setRoleName(Roles.ROLE_USER);
            userAuthorityRepository.save(user);

            Authority admin = new Authority();
            admin.setRoleName(Roles.ROLE_ADMIN);
            userAuthorityRepository.save(admin);




            UserAccount account1 = new UserAccount();
            UserAccount account2 = new UserAccount();
            UserAccount account3 = new UserAccount();


            account1.setFirstName("Samet1");
            account1.setLastName("AŞIK12345");
            account1.setEmail("sametrize1@hotmail.com");
            account1.setPassword("61536153-1");
            account1.setNickname("SEA6153-1");
            account1.setEnabled(true);
            account1.setRoles(Collections.singletonList(Roles.ROLE_ADMIN));

            account2.setFirstName("Samet2");
            account2.setLastName("AŞIK21345");
            account2.setEmail("sametrize2@hotmail.com");
            account2.setPassword("61536153-2");
            account2.setNickname("SEA6153-2");
            account2.setEnabled(true);
            account2.setRoles(Collections.singletonList(Roles.ROLE_MODERATOR));


            account3.setFirstName("Samet3");
            account3.setLastName("AŞIK32123");
            account3.setEmail("sametrize3@hotmail.com");
            account3.setPassword("61536153-3");
            account3.setNickname("SEA6153-3");
            account3.setEnabled(false);
            account3.setRoles(Collections.singletonList(Roles.ROLE_ADMIN));


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

            postServiceImpl.save(post1);
            postServiceImpl.save(post2);
            postServiceImpl.save(post3);

        }


    }

}
