package com.example.comp440proj.review;

import com.example.comp440proj.item.Item;
import com.example.comp440proj.item.ItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private ItemRepository itemRepository;

  @PostMapping("/add")
  public ResponseEntity<String> addReview(@RequestBody Review review, HttpSession session) {
    String username = (String) session.getAttribute("username");
    if (username == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
    }

    // Check if user is reviewing their own item
    Optional<Item> itemOpt = itemRepository.findById(review.getItemId());
    if (itemOpt.isPresent() && itemOpt.get().getUsername().equals(username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot review your own item.");
    }

    // Check if user has already submitted 2 reviews today
    LocalDate today = LocalDate.now();
    long reviewCount = reviewRepository.countByUsernameAndCreatedAt(username, today);
    if (reviewCount >= 2) {
      return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("You can only leave 2 reviews per day.");
    }

    review.setUsername(username);
    review.setCreatedAt(LocalDate.now());
    reviewRepository.save(review);

    return ResponseEntity.ok("Review submitted.");
  }

  @GetMapping("/item/{itemId}")
  public List<Review> getReviewsByItem(@PathVariable Long itemId) {
    return reviewRepository.findByItemId(itemId);
  }

  @GetMapping("/all-reviewed")
  public List<Object[]> getReviewedItems() {
    return reviewRepository.findReviewedItems();
  }

  @GetMapping("/users-only-poor")
  public ResponseEntity<List<String>> getUsersWithOnlyPoorReviews() {
    return ResponseEntity.ok(reviewRepository.findUsersWithOnlyPoorReviews());
  }
}
