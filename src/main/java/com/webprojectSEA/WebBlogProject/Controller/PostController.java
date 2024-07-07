package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.CommentRepository;
import com.webprojectSEA.WebBlogProject.Repostories.PostRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Services.CommentServices.CommentServiceImpl;
import com.webprojectSEA.WebBlogProject.Services.PostService.PostServiceImpl;
import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountServiceImpl;
import com.webprojectSEA.WebBlogProject.model.Category;
import com.webprojectSEA.WebBlogProject.model.Post;
import com.webprojectSEA.WebBlogProject.model.PostComment;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

    private final PostServiceImpl postServiceImpl;
    private final UserAccountRepository userAccountRepository;
    private final LoginController loginController;
    private final CommentRepository commentRepository;
    private final UserAccountServiceImpl userServiceImpl;
    private final PostRepository postRepository;
    private final CommentServiceImpl commentService;

    public PostController(PostServiceImpl postServiceImpl, UserAccountRepository userAccountRepository, LoginController loginController, CommentRepository commentRepository, UserAccountServiceImpl userServiceImpl, PostRepository postRepository, CommentServiceImpl commentService) {
        this.postServiceImpl = postServiceImpl;
        this.userAccountRepository = userAccountRepository;
        this.loginController = loginController;
        this.commentRepository = commentRepository;
        this.userServiceImpl = userServiceImpl;
        this.postRepository = postRepository;
        this.commentService = commentService;
    }

    @GetMapping("/posts")
    public String getPosts(
            @RequestParam(required = false) String sortOption,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String category,
            Model model) {

        String sortField = "createdAt";  // Default sort field
        String sortDirection = "desc";  // Default sort direction

        if (sortOption != null) {
            String[] parts = sortOption.split("_");
            if (parts.length == 2) {
                sortField = parts[0];
                sortDirection = parts[1];
            }
        }

        Category categoryEnum = null;
        if (category != null && !category.isEmpty()) {
            try {
                categoryEnum = Category.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }

        List<Post> post = postServiceImpl.getAllPosts(sortField, sortDirection, searchQuery, categoryEnum);
        model.addAttribute("post", post);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("query", searchQuery);
        model.addAttribute("category", categoryEnum);
        model.addAttribute("categories", Category.values());

        // Kullanıcı bilgisini modele ekle
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            UserAccount userAccount = userServiceImpl.findByUsernameOrEmail(auth.getName()) .orElseThrow(() -> new UsernameNotFoundException("Wrong Username or E-mail"));
            model.addAttribute("userAccount", userAccount);
        }

        return "form";
    }


    @GetMapping("posts/{id}")
    public String getPost(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Post> optionalPost = postServiceImpl.getById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<PostComment> postComments = commentRepository.findByPost(post);
            String timeAgo = getTimeAgo(post.getCreatedAt());
            for (PostComment comment : postComments) {
                getTimeAgo(comment.getCreatedAt());
            }
            boolean isPostOwner = false;
            UserAccount userAccount = null;
            Long loggedInUserId = loginController.getLoggedInUserId(authentication);

            if (authentication != null && authentication.isAuthenticated()) {
                String currentUsername = authentication.getName();
                userAccount = userAccountRepository.findByNickname(currentUsername).orElse(null);

                if (userAccount != null) {
                    isPostOwner = post.getUserAccount().getNickname().equals(currentUsername);
                    loggedInUserId = userAccount.getId();
                }
            }
            model.addAttribute("timeAgo", timeAgo);
            model.addAttribute("postComments", postComments);
            model.addAttribute("isPostOwner", isPostOwner);
            model.addAttribute("userAccount", userAccount);
            model.addAttribute("userAccountId", loggedInUserId);
            model.addAttribute("post", post);


            return "post"; // post.html
        } else {
            return "404";
        }
    }
    private String getTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);

        if (days > 0) {
            if (days == 1) {
                return "1 day ago";
            } else {
                return days + " days ago";
            }
        } else if (hours > 0) {
            if (hours == 1) {
                return "1 hour ago";
            } else {
                return hours + " hours ago";
            }
        } else {
            if (minutes < 1) {
                return "just now";
            } else if (minutes == 1) {
                return "1 minute ago";
            } else {
                return minutes + " minutes ago";
            }
        }
    }

    @GetMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String createNewPost(Model model, Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findById(loggedInUserId);
        if (optionalUserAccount.isPresent()) {
            Post post = new Post();
            post.setUserAccount(optionalUserAccount.get());
            model.addAttribute("post", post);
            return "post_new";
        } else {
            return "404";
        }
    }

    @PostMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String saveNewPost(@ModelAttribute Post post, @RequestParam("file") MultipartFile file, Authentication authentication) throws IOException {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        Optional<UserAccount> optionalUserAccount = userAccountRepository.findById(loggedInUserId);
        if (optionalUserAccount.isPresent()) {
            post.setUserAccount(optionalUserAccount.get());
            postServiceImpl.save(post, file);
            return "redirect:/posts/" + post.getId();
        } else {
            return "404";
        }
    }

    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String getPostForEdit(@PathVariable Long id, Model model, Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        Optional<Post> optionalPost = postServiceImpl.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (post.getUserAccount().getId().equals(loggedInUserId)) {
                model.addAttribute("post", post);
                return "post_edit";
            } else {
                return "403";
            }
        } else {
            return "404";
        }
    }

    @PostMapping("/posts/{id}/update")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute("post") Post post,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             @RequestParam(value = "removePhoto", required = false) boolean removePhoto,
                             Authentication authentication) throws IOException {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        Optional<Post> optionalPost = postServiceImpl.getById(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();
            if (existingPost.getUserAccount().getId().equals(loggedInUserId)) {
                existingPost.setTitle(post.getTitle());
                existingPost.setExplanation(post.getExplanation());
                existingPost.setCategory(post.getCategory());


                if (removePhoto) {
                    existingPost.setImageUrl(null);
                }


                if (file != null && !file.isEmpty()) {
                    String photoUrl = savePhoto(file);
                    existingPost.setImageUrl(photoUrl);
                }

                postServiceImpl.save(existingPost, file);
                return "redirect:/posts/" + id;
            } else {
                return "403";
            }
        } else {
            return "404";
        }
    }
    private String savePhoto(MultipartFile photo) throws IOException {
        return "/path/to/photo/" + photo.getOriginalFilename();
    }
    @PostMapping("/{postId}/comments/{commentId}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Authentication authentication, Model model) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        Optional<PostComment> optionalComment = Optional.ofNullable(commentService.getCommentById(commentId));
        if (optionalComment.isPresent()) {
            PostComment comment = optionalComment.get();
            if (comment.getUserAccount().getId().equals(loggedInUserId)) {
                commentService.deleteComment(commentId);
                model.addAttribute("message", "Comment deleted successfully");
            } else {
                model.addAttribute("error", "You are not authorized to delete this comment");
                return "403";
            }
        } else {
            model.addAttribute("error", "Comment not found");
            return "404";
        }
        return "redirect:/posts/" + postId;
    }



    @GetMapping("/my/posts")
    @PreAuthorize("isAuthenticated()")
    public String myPosts(Model model, Authentication authentication, @RequestParam(required = false) String sortOption,
                          @RequestParam(required = false) String searchQuery,
                          @RequestParam(required = false) String category) {
        String sortField = "createdAt";  // Default sort field
        String sortDirection = "desc";  // Default sort direction

        if (sortOption != null) {
            String[] parts = sortOption.split("_");
            if (parts.length == 2) {
                sortField = parts[0];
                sortDirection = parts[1];
            }
        }

        Category categoryEnum = null;
        if (category != null && !category.isEmpty() && !"all".equalsIgnoreCase(category)) {
            try {
                categoryEnum = Category.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                // handle exception if the category is not valid
            }
        }

        UserAccount loggedInUser = loginController.getLoggedInUser(authentication);
        List<Post> userPosts = postServiceImpl.getPostsByUserId(loggedInUser.getId(), sortField, sortDirection, searchQuery, categoryEnum);
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        model.addAttribute("userPosts", userPosts);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("query", searchQuery);
        model.addAttribute("category", categoryEnum);
        model.addAttribute("categories", Category.values());
        model.addAttribute("userAccountId", loggedInUserId);

        return "my_posts";
    }



    @PostMapping("posts/{id}/like")
    public String likePost(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String loggedInUserNicknameOrEmail = loginController.getLoggedInUserNickname(authentication);
        if (loggedInUserNicknameOrEmail == null) {
            return "redirect:/login";
        }
        postServiceImpl.likePost(id, loggedInUserNicknameOrEmail);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping("posts/{id}/dislike")
    public String dislikePost(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = authentication.getName();
        postServiceImpl.dislikePost(id, username);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping("posts/{id}/unlike")
    public String unlikePost(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String loggedInUserNicknameOrEmail = loginController.getLoggedInUserNickname(authentication);
        if (loggedInUserNicknameOrEmail == null) {
            return "redirect:/login";
        }
        postServiceImpl.unlikePost(id, loggedInUserNicknameOrEmail);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping("posts/{id}/undislike")
    public String undislikePost(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = authentication.getName();
        postServiceImpl.undislikePost(id, username);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
    }



