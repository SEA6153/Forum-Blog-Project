package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Services.PostServiceImpl;
import com.webprojectSEA.WebBlogProject.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private PostServiceImpl postServiceImpl;

    public HomeController(PostServiceImpl postServiceImpl){
        this.postServiceImpl = postServiceImpl;
    }

    @GetMapping("/")
    public String home(Model model){
        List<Post> posts = postServiceImpl.getAll();
        model.addAttribute("posts", posts);
        return "home";
    }




}
