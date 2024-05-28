package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Repostories.CommentRepository;
import com.webprojectSEA.WebBlogProject.Repostories.PostRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Services.CommentServices.CommentServiceImpl;
import com.webprojectSEA.WebBlogProject.Services.PostService.PostServiceImpl;
import com.webprojectSEA.WebBlogProject.model.Post;
import com.webprojectSEA.WebBlogProject.model.PostComment;
import com.webprojectSEA.WebBlogProject.model.UserAccount;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserAccountRepository userAccountRepository;
    private final PostRepository postRepository;
    private final LoginController loginController;
    private final CommentServiceImpl commentServiceImpl;
    private final PostServiceImpl postServiceImpl;

    public CommentController(CommentRepository commentRepository, UserAccountRepository userAccountRepository, PostRepository postRepository, LoginController loginController, CommentServiceImpl commentServiceImpl, PostServiceImpl postServiceImpl) {
        this.commentRepository = commentRepository;
        this.userAccountRepository = userAccountRepository;
        this.postRepository = postRepository;
        this.loginController = loginController;
        this.commentServiceImpl = commentServiceImpl;
        this.postServiceImpl = postServiceImpl;
    }

    @PostMapping("/posts/{postId}")
    public String addComment(@PathVariable Long postId, @RequestParam String text, @RequestParam String nickname, RedirectAttributes redirectAttributes) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found!"));
        UserAccount userAccount = userAccountRepository.findByNickname(nickname).orElseThrow(() -> new RuntimeException("User not found!"));

        PostComment postComment = new PostComment();
        postComment.setText(text);
        postComment.setUserAccount(userAccount);
        postComment.setPost(post);
        commentRepository.save(postComment);

        redirectAttributes.addFlashAttribute("message", "Comment added Successfully");

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestParam String text, Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        PostComment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found!"));
        if (!comment.getUserAccount().getId().equals(loggedInUserId)) {
            return "403";
        }
        comment.setText(text);
        commentRepository.save(comment);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Authentication authentication) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        PostComment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found!"));
        if (!comment.getUserAccount().getId().equals(loggedInUserId)) {
            return "403";
        }
        commentRepository.delete(comment);
        return "redirect:/posts/" + postId;
    }

    @GetMapping("my/comments")
    @PreAuthorize("isAuthenticated()")
    public String myComments(Model model, Authentication authentication, Long id) {
        Long loggedInUserId = loginController.getLoggedInUserId(authentication);
        List<PostComment> postComments = commentServiceImpl.getCommentsByUserId(loggedInUserId);

        // Her yorumun hangi gönderiye ait olduğunu belirleyin
        for (PostComment comment : postComments) {
            Long postId = comment.getPost().getId();
            Optional<Post> optionalPost = postServiceImpl.getById(postId);
            optionalPost.ifPresent(post -> comment.setPostTitle(post.getTitle()));
        }

        model.addAttribute("userComments", postComments);
        return "my_comments";
    }

}
