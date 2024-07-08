
package com.webprojectSEA.WebBlogProject.Services.PostService;


import com.webprojectSEA.WebBlogProject.Repostories.PostRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Repostories.UserLikeDislikeRepository;
import com.webprojectSEA.WebBlogProject.Services.AWSServices.AwsS3Service;
import com.webprojectSEA.WebBlogProject.Model.Category;
import com.webprojectSEA.WebBlogProject.Model.Post;
import com.webprojectSEA.WebBlogProject.Model.PostSpecification;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserLikeDislikeRepository userLikeDislikeRepository;
    private final AwsS3Service s3Service;
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    public PostServiceImpl(PostRepository postRepository, UserAccountRepository userAccountRepository, UserLikeDislikeRepository userLikeDislikeRepository, AwsS3Service s3Service) {
        this.postRepository = postRepository;
        this.userAccountRepository = userAccountRepository;
        this.userLikeDislikeRepository = userLikeDislikeRepository;
        this.s3Service = s3Service;
    }

    @Override
    public Optional<Post> getById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Optional<Post> getByUserAccID(Long userID){
        return postRepository.findByUserAccountId(userID);    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public Post save(Post post, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String imageUrl = s3Service.uploadFile(file, "postImages/");
            post.setImageUrl(imageUrl);
        }
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postID, Long loggedInUserId) {
        Optional<Post> postOptional = postRepository.findById(postID);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            postRepository.delete(post);
        } else {
            throw new IllegalArgumentException("Post not found");
        }
    }

    @Override
    public List<Post> getPostsByUserId(Long userId, String sortField, String sortDirection, String searchQuery, Category category) {
        Specification<Post> spec = Specification.where(PostSpecification.byUserId(userId));

        if (searchQuery != null && !searchQuery.isEmpty()) {
            spec = spec.and(PostSpecification.containsText(searchQuery));
        }

        if (category != null) {
            spec = spec.and(PostSpecification.hasCategory(category));
        } else {
            spec = spec.and(PostSpecification.hasAnyCategory());
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return postRepository.findAll(spec, sort);
    }

    @Override
    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public UserAccount findByNickname(String nickname) {
        return userAccountRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("User not found: " + nickname));
    }


    @Override
    @Transactional
    public void likePost(Long postId, String username) {
        logger.info("Liking post for username: {}", username);
        Post post = findById(postId);
        UserAccount user = userAccountRepository.findByEmail(username)
                .or(() -> userAccountRepository.findByNickname(username))
                .orElseThrow(() -> {
                    logger.error("User not found for username: {}", username);
                    return new RuntimeException("User not found");
                });

        if (post.getLikes().contains(user)) {
            post.getLikes().remove(user);
            post.setLikeCount(post.getLikeCount() - 1);
            logger.info("User {} removed like from post {}", username, postId);
        } else {
            post.getLikes().add(user);
            post.setLikeCount(post.getLikeCount() + 1);
            logger.info("User {} liked post {}", username, postId);

            if (post.getDislikes().contains(user)) {
                post.getDislikes().remove(user);
                post.setDislikeCount(post.getDislikeCount() - 1);
                logger.info("User {} removed dislike from post {}", username, postId);
            }
        }
        postRepository.save(post);
    }

    @Override
    public void dislikePost(Long postId, String username) {
        logger.info("Disliking post for username: {}", username);
        Post post = findById(postId);
        UserAccount user = userAccountRepository.findByEmail(username)
                .or(() -> userAccountRepository.findByNickname(username))
                .orElseThrow(() -> {
                    logger.error("User not found for username: {}", username);
                    return new RuntimeException("User not found");
                });

        if (post.getDislikes().contains(user)) {
            post.getDislikes().remove(user);
            post.setDislikeCount(post.getDislikeCount() - 1);
            logger.info("User {} removed dislike from post {}", username, postId);
        } else {
            post.getDislikes().add(user);
            post.setDislikeCount(post.getDislikeCount() + 1);
            logger.info("User {} disliked post {}", username, postId);

            if (post.getLikes().contains(user)) {
                post.getLikes().remove(user);
                post.setLikeCount(post.getLikeCount() - 1);
                logger.info("User {} removed like from post {}", username, postId);
            }
        }
        postRepository.save(post);
    }

    @Override
    public void unlikePost(Long postId, String username) {
        Post post = findById(postId);
        UserAccount user = userAccountRepository.findByEmail(username)
                .or(() -> userAccountRepository.findByNickname(username))
                .orElseThrow(() -> {
                    logger.error("User not found for username: {}", username);
                    return new RuntimeException("User not found");
                });

        if (post.getLikes().contains(user)) {
            post.getLikes().remove(user);
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
        }
    }

    @Override
    public void undislikePost(Long postId, String username) {
        Post post = findById(postId);
        UserAccount user = userAccountRepository.findByEmail(username)
                .or(() -> userAccountRepository.findByNickname(username))
                .orElseThrow(() -> {
                    logger.error("User not found for username: {}", username);
                    return new RuntimeException("User not found");
                });

        if (post.getDislikes().contains(user)) {
            post.getDislikes().remove(user);
            post.setDislikeCount(post.getDislikeCount() - 1);
            postRepository.save(post);
        }
    }
    @Override
    public String getTimeAgo(LocalDateTime createdAt) {
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

    @Override
    public List<Post> getAllPosts(String sortField, String sortDirection, String query, Category category) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        if (query != null && !query.isEmpty() && category != null) {
            return postRepository.findByTitleContainingAndCategory(query, category, sort);
        } else if (query != null && !query.isEmpty()) {
            return postRepository.findByTitleContaining(query, sort);
        } else if (category != null) {
            return postRepository.findByCategory(category, sort);
        }
        return postRepository.findAll(sort);
    }

    @Override
    public Post seedDatasave(Post seedDataPost) {
        if (seedDataPost.getId() == null) {
            seedDataPost.setCreatedAt(LocalDateTime.now());
        }
        return postRepository.save(seedDataPost);
    }

    @Override
    public List<Post> searchPosts(String query, String sortField, String sortDirection) {
        List<Post> posts = postRepository.findByTitleContainingIgnoreCase(query);
        // Sort the list according to the provided field and direction
        Comparator<Post> comparator;
        switch (sortField) {
            case "editDate":
                comparator = Comparator.comparing(Post::getEditDate);
                break;
            case "likes":
                comparator = Comparator.comparing(Post::getLikeCount);
                break;
            case "createdAt":
            default:
                comparator = Comparator.comparing(Post::getCreatedAt);
                break;
        }
        if ("desc".equals(sortDirection)) {
            comparator = comparator.reversed();
        }
        return posts.stream().sorted(comparator).collect(Collectors.toList());
    }
}

