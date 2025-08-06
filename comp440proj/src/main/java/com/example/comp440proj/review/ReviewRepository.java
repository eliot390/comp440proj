package com.example.comp440proj.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findByItemId(Long itemId);
  long countByUsernameAndCreatedAt(String username, LocalDate createdAt);

}
