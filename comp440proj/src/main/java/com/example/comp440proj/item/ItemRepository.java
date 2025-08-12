package com.example.comp440proj.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  List<Item> findByUserUsername(String username);

  @Query("SELECT COUNT(i) FROM Item i WHERE i.user.username = :username AND i.createdAt = :date")
  int countItemsByUserAndDate(@Param("username") String username, @Param("date") LocalDate date);


  // ✅ FIXED: JSON_CONTAINS needs JSON_QUOTE(value) and a path '$'
  @Query(value = "SELECT * FROM items WHERE JSON_CONTAINS(category, :category, '$')", nativeQuery = true)
  List<Item> findByCategoryContaining(@Param("category") String category);

  Optional<Item> findById(Long id);

  // 1) Most expensive items per category (JSON categories; ties included)
  @Query(value = """
  WITH item_cat AS (
    SELECT i.id, i.title, i.description, i.price, i.username, i.created_at,
           jt.cat AS category
    FROM items i
    JOIN JSON_TABLE(i.category, '$[*]'
          COLUMNS (cat VARCHAR(255) PATH '$')) jt
  ),
  ranked AS (
    SELECT ic.*,
           ROW_NUMBER() OVER (
             PARTITION BY ic.category
             ORDER BY ic.price DESC, ic.created_at DESC, ic.id ASC
           ) AS rn
    FROM item_cat ic
  )
  -- ⬇️ order the columns exactly how you want them in the output
  SELECT id, title, description, price, username, category, created_at
  FROM ranked
  WHERE rn = 1
  ORDER BY price DESC 
  """, nativeQuery = true)
  List<com.example.comp440proj.report.CatItemView> mostExpensivePerCategory();

  // 2) Same user posted two different items on the SAME date:
// one containing catX, the other containing catY
  @Query(value = """
  SELECT DISTINCT i1.username
  FROM items i1
  JOIN items i2
    ON i1.username = i2.username
   AND i1.created_at = i2.created_at
   AND i1.id <> i2.id
  WHERE JSON_CONTAINS(i1.category, JSON_QUOTE(:catX), '$')
    AND JSON_CONTAINS(i2.category, JSON_QUOTE(:catY), '$')
  """, nativeQuery = true)
  List<String> usersWithCatXandCatYSameDay(@Param("catX") String catX,
                                           @Param("catY") String catY);

}