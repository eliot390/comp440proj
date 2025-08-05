package com.example.comp440proj.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  List<Item> findByUserUsername(String username);

  @Query("SELECT COUNT(i) FROM Item i WHERE i.user.username = :username AND i.createdAt = :date")
  int countItemsByUserAndDate(@Param("username") String username, @Param("date") LocalDate date);

  @Query(value = "SELECT * FROM items WHERE JSON_CONTAINS(category, :category)", nativeQuery = true)
  List<Item> findByCategoryContaining(@Param("category") String category);


}