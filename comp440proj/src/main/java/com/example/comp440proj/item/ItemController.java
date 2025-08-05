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
  public ResponseEntity<String> addItem(@RequestBody ItemRequest request) {
    Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

    if (userOpt.isEmpty()) {
      return ResponseEntity.badRequest().body("User not found.");
    }

    LocalDate today = LocalDate.now();
    int itemsToday = itemRepository.countItemsByUserAndDate(request.getUsername(), today);

    if (itemsToday >= 2) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only post 2 items per day.");
    }

    Item item = new Item();
    item.setTitle(request.getTitle());
    item.setDescription(request.getDescription());
    item.setCategoryList(request.getCategory()); // converts List<String> to JSON
    item.setPrice(request.getPrice());
    item.setUser(userOpt.get());
    item.setCreatedAt(today);

    itemRepository.save(item);
    return ResponseEntity.ok("Item added successfully.");
  }


  @GetMapping("/user")
  public ResponseEntity<List<Item>> getUserItems(@RequestParam String username) {
    return ResponseEntity.ok(itemRepository.findByUserUsername(username));
  }

  @GetMapping("/category")
  public ResponseEntity<List<Item>> getItemsByCategory(@RequestParam String name) {
    // JSON_CONTAINS requires the value to be in double quotes
    String quotedName = "\"" + name + "\"";
    List<Item> items = itemRepository.findByCategoryContaining(quotedName);
    return ResponseEntity.ok(items);
  }

}