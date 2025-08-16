package com.example.comp440proj.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findByItemId(Long itemId);
  long countByUsernameAndCreatedAt(String username, LocalDate createdAt);

  @Query("SELECT i.title, i.description, i.category, i.price, r.rating, r.comment " +
          "FROM Item i LEFT JOIN Review r ON i.id = r.itemId " +
          "WHERE r.rating IS NOT NULL")
  List<Object[]> findReviewedItems();

  @Query("""
         SELECT r.username
         FROM Review r
         GROUP BY r.username
         HAVING COUNT(r) > 0
            AND SUM(CASE WHEN LOWER(COALESCE(r.rating, '')) <> 'poor' THEN 1 ELSE 0 END) = 0
         """)
  List<String> findUsersWithOnlyPoorReviews();
}
