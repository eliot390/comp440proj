package com.example.comp440proj.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.example.comp440proj.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "items")
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String description;

  @Column(insertable = false, updatable = false)
  private String username;

  @Column(columnDefinition = "json")
  private String category;

  @Column(name = "price")
  private BigDecimal price;

  @ManyToOne
  @JoinColumn(name = "username", referencedColumnName = "username")
  private User user;

  private LocalDate createdAt = LocalDate.now();

  // Getters and setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
  }

  public List<String> getCategoryList() {
    if (category == null) return null;
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(category, new TypeReference<List<String>>() {});
    } catch (IOException e) {
      throw new RuntimeException("Failed to parse category JSON", e);
    }
  }

  public void setCategoryList(List<String> categoryList) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      this.category = mapper.writeValueAsString(categoryList);
    } catch (IOException e) {
      throw new RuntimeException("Failed to convert category list to JSON", e);
    }
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}