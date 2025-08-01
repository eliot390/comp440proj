package com.example.comp440proj.item;

import com.example.comp440proj.user.User;
import com.example.comp440proj.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private UserRepository userRepository;

  @PostMapping("/add")
  public ResponseEntity<String> addItem(@RequestBody Item item, @RequestParam String username) {
    Optional<User> userOpt = userRepository.findByUsername(username);

    if (userOpt.isEmpty()) {
      return ResponseEntity.badRequest().body("User not found.");
    }

    LocalDate today = LocalDate.now();
    int itemsToday = itemRepository.countItemsByUserAndDate(username, today);

    if (itemsToday >= 2) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only post 2 items per day.");
    }

    item.setUser(userOpt.get());
    item.setCreatedAt(today);
    itemRepository.save(item);
    return ResponseEntity.ok("Item added successfully.");
  }

  @GetMapping("/user")
  public ResponseEntity<List<Item>> getUserItems(@RequestParam String username) {
    return ResponseEntity.ok(itemRepository.findByUserUsername(username));
  }
}