package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Services.AWSServices.AwsS3Service;
import com.webprojectSEA.WebBlogProject.Services.CommentServices.CommentServiceImpl;
import com.webprojectSEA.WebBlogProject.Services.PostService.PostServiceImpl;
import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountServiceImpl;
import com.webprojectSEA.WebBlogProject.model.Category;
import com.webprojectSEA.WebBlogProject.model.Post;
import com.webprojectSEA.WebBlogProject.model.PostComment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
public class CommentController {

    private final CommentServiceImpl commentService;
    private final AwsS3Service awsS3Service;
    private final LoginController loginController;
    private final PostServiceImpl postService;
    private final UserAccountServiceImpl userAccountService;

    public CommentController(CommentServiceImpl commentService, AwsS3Service awsS3Service, LoginController loginController, PostServiceImpl postService, UserAccountServiceImpl userAccountService) {
        this.commentService = commentService;
        this.awsS3Service = awsS3Service;
        this.loginController = loginController;
        this.postService = postService;
        this.userAccountService = userAccountService;
    }

    @GetMapping("my/comments")
    @PreAuthorize("isAuthenticated()")
    public String myComments(
            @RequestParam(required = false) String sortOption,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String category,
            Model model, Authentication authentication) {

        Long loggedInUserId = loginController.getLoggedInUserId(authentication);

        // Default sort field and direction
        String sortField = "createdAt";
        String sortDirection = "desc";

        // Parse sortOption if present
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
                // Geçersiz kategori durumu
            }
        }

        List<PostComment> postComments = commentService.getCommentsByUserId(loggedInUserId, sortField, sortDirection, searchQuery, categoryEnum);

        for (PostComment comment : postComments) {
            Long postId = comment.getPost().getId();
            Optional<Post> optionalPost = postService.getById(postId);
            optionalPost.ifPresent(post -> comment.setPostTitle(post.getTitle()));
        }

        model.addAttribute("userComments", postComments);
        model.addAttribute("userAccountId", loggedInUserId);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("query", searchQuery);
        model.addAttribute("category", categoryEnum);
        model.addAttribute("categories", Category.values());

        return "my_comments";
    }

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@PathVariable Long postId,
                             @RequestParam(value = "text", required = false) String text,
                             @RequestParam("photo") MultipartFile photo,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        String photoUrl = null;

        if ((text == null || text.isEmpty()) && photo.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must provide text or photo.");
            return "redirect:/posts/" + postId;
        }

        if (!photo.isEmpty()) {
            try {
                photoUrl = awsS3Service.uploadFile(photo, "comments");
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "Photo upload failed.");
                return "redirect:/posts/" + postId;
            }
        }

        commentService.addComment(postId, username, email, text, photoUrl, loggedInUserId);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editComment(@PathVariable Long postId,
                              @PathVariable Long commentId,
                              @RequestParam(value = "text", required = false) String text,
                              @RequestParam("photo") MultipartFile photo,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        PostComment comment = commentService.getCommentById(commentId);

        if (comment != null && comment.getUserAccount().getId().equals(loggedInUserId)) {
            String photoUrl = comment.getPhotoUrl();

            if (!photo.isEmpty()) {
                try {
                    photoUrl = awsS3Service.uploadFile(photo, "comments");
                    if (comment.getPhotoUrl() != null) {
                        awsS3Service.deleteFile(comment.getPhotoUrl());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("errorMessage", "Photo upload failed.");
                    return "redirect:/posts/" + postId;
                }
            }

            commentService.updateComment(commentId, text, photoUrl);
        }

        return "redirect:/posts/" + postId;
    }


    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable Long postId,
                                @PathVariable Long commentId,
                                Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        PostComment comment = commentService.getCommentById(commentId);

        if (comment != null && comment.getUserAccount().getId().equals(loggedInUserId)) {
            if (comment.getPhotoUrl() != null) {
                awsS3Service.deleteFile(comment.getPhotoUrl());
            }
            commentService.deleteComment(commentId);
        }

        return "redirect:/posts/" + postId;
    }


    @PostMapping("/my/comments/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editMyComment(@PathVariable Long id, @RequestParam String text, @RequestParam(required = false) MultipartFile photo,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        PostComment comment = commentService.getCommentById(id);

        if (comment != null && comment.getUserAccount().getId().equals(loggedInUserId)) {
            String photoUrl = comment.getPhotoUrl();

            if (!photo.isEmpty()) {
                try {
                    photoUrl = awsS3Service.uploadFile(photo, "comments");
                    if (comment.getPhotoUrl() != null) {
                        awsS3Service.deleteFile(comment.getPhotoUrl());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("errorMessage", "Photo upload failed.");
                    return "redirect:/my/comments";
                }
            }

            commentService.updateComment(id, text, photoUrl);
        }
        return "redirect:/my/comments";  // Redirect to my_comments page after editing
    }

    @PostMapping("/my/comments/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteMyComment(@RequestParam Long id, Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        PostComment comment = commentService.getCommentById(id);

        if (comment != null && comment.getUserAccount().getId().equals(loggedInUserId)) {
            if (comment.getPhotoUrl() != null) {
                awsS3Service.deleteFile(comment.getPhotoUrl());
            }
            commentService.deleteComment(id);
        }
        return "redirect:/my/comments";  // Redirect to my_comments page after deleting
    }

    @PostMapping("comments/{id}/like")
    public String likeComment(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String loggedInUserNicknameOrEmail = loginController.getLoggedInUserNickname(authentication);
        if (loggedInUserNicknameOrEmail == null) {
            return "redirect:/login";
        }
        commentService.likeComment(id, loggedInUserNicknameOrEmail);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping("comments/{id}/dislike")
    public String dislikeComment(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String loggedInUserNicknameOrEmail = loginController.getLoggedInUserNickname(authentication);
        if (loggedInUserNicknameOrEmail == null) {
            return "redirect:/login";
        }
        commentService.dislikeComment(id, loggedInUserNicknameOrEmail);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping("comments/{id}/unlike")
    public String unlikeComment(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String loggedInUserNicknameOrEmail = loginController.getLoggedInUserNickname(authentication);
        if (loggedInUserNicknameOrEmail == null) {
            return "redirect:/login";
        }
        commentService.unlikeComment(id, loggedInUserNicknameOrEmail);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping("comments/{id}/undislike")
    public String undislikeComment(@PathVariable Long id, Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String loggedInUserNicknameOrEmail = loginController.getLoggedInUserNickname(authentication);
        if (loggedInUserNicknameOrEmail == null) {
            return "redirect:/login";
        }
        commentService.undislikeComment(id, loggedInUserNicknameOrEmail);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
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
}
