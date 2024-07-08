package com.webprojectSEA.WebBlogProject.Model;

import org.springframework.data.jpa.domain.Specification;

public class PostSpecification {

    public static Specification<Post> byUserId(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userAccount").get("id"), userId);
    }

    public static Specification<Post> containsText(String searchText) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(root.get("title"), "%" + searchText + "%"),
                        criteriaBuilder.like(root.get("explanation"), "%" + searchText + "%")
                );
    }

    public static Specification<Post> hasCategory(Category category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category"), category);
    }

    public static Specification<Post> hasAnyCategory() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("category"));
    }
}