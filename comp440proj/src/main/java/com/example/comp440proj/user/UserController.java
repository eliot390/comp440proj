package com.example.comp440proj.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/users")
  /**
   * http://localhost:8080/users
   **/
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userRepository.findAll();
    return ResponseEntity.ok(users);
  }

  @PostMapping("/register")
  /**
   * http://localhost:8080/register
   * @params
   * {
   *   "username": "alice01",
   *   "password": "password123",
   *   "firstName": "Alice",
   *   "lastName": "Johnson",
   *   "email": "alice@example.com"
   * }
   **/
  public ResponseEntity<String> registerUser(@RequestBody User user) {
    if (userRepository.existsById(user.getUsername())) {
      return ResponseEntity.badRequest().body("Username already exists.");
    }

    if (userRepository.existsByEmail(user.getEmail())) {
      return ResponseEntity.badRequest().body("Email already in use.");
    }

    userRepository.save(user);  // password stored as plain text
    return ResponseEntity.ok("User registered successfully.");
  }

  @PostMapping("/login")
  /**
   * http://localhost:8080/login
   * @params
   * {
   *   "username": "alice01",
   *   "password": "password123"
   * }
   **/
  public ResponseEntity<String> login(@RequestBody User loginRequest, HttpSession session) {
    Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (user.getPassword().equals(loginRequest.getPassword())) {
        session.setAttribute("username", user.getUsername());
        return ResponseEntity.ok("Login successful.");
      }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpSession session) {
    session.invalidate();
    return ResponseEntity.ok("Successfully logged out.");
  }

  @GetMapping("/current-user")
  public ResponseEntity<String> getCurrentUser(HttpSession session) {
    String username = (String) session.getAttribute("username");
    if (username != null) {
      return ResponseEntity.ok(username);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }
  }
}
