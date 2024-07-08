package com.webprojectSEA.WebBlogProject.Services.CommentServices;

import com.webprojectSEA.WebBlogProject.Repostories.CommentRepository;
import com.webprojectSEA.WebBlogProject.Repostories.PostRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Services.AWSServices.AwsS3Service;
import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountServiceImpl;
import com.webprojectSEA.WebBlogProject.Model.Category;
import com.webprojectSEA.WebBlogProject.Model.Post;
import com.webprojectSEA.WebBlogProject.Model.PostComment;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final UserAccountRepository userRepository;
    private final AwsS3Service awsS3Service;
    private final PostRepository postRepository;
    private final UserAccountServiceImpl userAccountService;

    public CommentServiceImpl(CommentRepository commentRepository, UserAccountRepository userRepository, AwsS3Service awsS3Service, PostRepository postRepository, UserAccountServiceImpl userAccountService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.awsS3Service = awsS3Service;
        this.postRepository = postRepository;
        this.userAccountService = userAccountService;
    }

    @Override
    public List<PostComment> getCommentsByUserId(Long userId, String sortField, String sortDirection, String query, Category category) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);

        UserAccount userAccount = userAccountService.getUserById(userId);

        if (category == null || category.name().equals("ALL")) {
            return commentRepository.findByUserAccountAndTextContaining(userAccount, query, sort);
        } else {
            return commentRepository.findByUserAccountAndTextContainingAndPostCategory(userAccount, query, category, sort);
        }
    }
    @Override
    public PostComment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }
    @Override
    public List<PostComment> findCommentsByUsername(String username) {
        return commentRepository.findByUserAccountNickname(username);
    }

    @Override
    public void addComment(Long postId, String username, String email, String text, String photoUrl, Long userId ) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found!"));
        UserAccount userAccount = userRepository.findByNicknameOrEmail(username, email).orElseThrow(() -> new RuntimeException("User not found!"));
        UserAccount userAccountId = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));;
        PostComment postComment = new PostComment();
        postComment.setId(userAccountId.getId());
        postComment.setText(text);
        postComment.setUserAccount(userAccount);
        postComment.setPost(post);
        postComment.setPhotoUrl(photoUrl);
        postComment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(postComment);
    }

    @Override
    public void updateComment(Long commentId, String text, String photoUrl) {
        PostComment postComment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found!"));
        postComment.setText(text);
        postComment.setPhotoUrl(photoUrl);
        commentRepository.save(postComment);
    }
    public void deleteComment(Long id) {
        Optional<PostComment> optionalComment = Optional.ofNullable(getCommentById(id));
        if (optionalComment.isPresent()) {
            PostComment comment = optionalComment.get();
            if (comment.getPhotoUrl() != null) {
                awsS3Service.deleteFile(comment.getPhotoUrl());
            }
            commentRepository.deleteById(id);
        }
    }
    @Transactional
    @Override
    public void likeComment(Long commentId, String username) {
        PostComment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment Not Found!"));
        UserAccount user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByNickname(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (comment.getLikes().contains(user)) {
            comment.getLikes().remove(user);
            comment.setLikeCount(comment.getLikeCount() - 1);
        } else {
            comment.getLikes().add(user);
            comment.setLikeCount(comment.getLikeCount() + 1);
            if (comment.getDislikes().contains(user)) {
                comment.getDislikes().remove(user);
                comment.setDislikeCount(comment.getDislikeCount() - 1);
            }
        }
        commentRepository.save(comment);
    }


    @Transactional
    @Override
    public void dislikeComment(Long commentId, String username) {
        PostComment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment Not Found!"));
        UserAccount user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByNickname(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (comment.getDislikes().contains(user)) {
            comment.getDislikes().remove(user);
            comment.setDislikeCount(comment.getDislikeCount() - 1);
        } else {
            comment.getDislikes().add(user);
            comment.setDislikeCount(comment.getDislikeCount() + 1);
            if (comment.getLikes().contains(user)) {
                comment.getLikes().remove(user);
                comment.setLikeCount(comment.getLikeCount() - 1);
            }
        }
        commentRepository.save(comment);
    }

    @Transactional
    @Override
    public void unlikeComment(Long commentId, String username) {
        PostComment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment Not Found!"));
        UserAccount user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByNickname(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (comment.getLikes().contains(user)) {
            comment.getLikes().remove(user);
            comment.setLikeCount(comment.getLikeCount() - 1);
            commentRepository.save(comment);
        }
    }

    @Transactional
    @Override
    public void undislikeComment(Long commentId, String username) {
        PostComment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment Not Found!"));
        UserAccount user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByNickname(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (comment.getDislikes().contains(user)) {
            comment.getDislikes().remove(user);
            comment.setDislikeCount(comment.getDislikeCount() - 1);
            commentRepository.save(comment);
        }
    }
    @Override
    public String getTimeAgoComment(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);
        long days = duration.toDays();

        if (days == 0) {
            return "today";
        } else if (days == 1) {
            return "1 day ago";
        } else {
            return days + " days ago";
        }
    }
}
