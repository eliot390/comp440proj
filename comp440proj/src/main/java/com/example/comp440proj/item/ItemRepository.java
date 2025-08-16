package com.example.comp440proj.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  List<Item> findByUserUsername(String username);

  @Query("SELECT COUNT(i) FROM Item i WHERE i.user.username = :username AND i.createdAt = :date")
  int countItemsByUserAndDate(@Param("username") String username, @Param("date") LocalDate date);

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
  SELECT id, title, description, price, username, category, created_at
  FROM ranked
  WHERE rn = 1
  ORDER BY price DESC 
  """, nativeQuery = true)
  List<com.example.comp440proj.report.CatItemView> mostExpensivePerCategory();

  // 2) Same user posted two different items on the SAME date
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

  // 3)
  @Query(value = """
    SELECT i.id, i.title, i.description, i.category, i.price, r.rating, r.comment
    FROM items i
    JOIN review r ON i.id = r.item_id
    WHERE r.username = :username
      AND r.rating IN ('excellent', 'good')
    """, nativeQuery = true)
  List<Map<String, Object>> findExcellentOrGoodByUser(@Param("username") String username);

  // 4)
  @Query(value = """
        SELECT username, item_count
        FROM (
            SELECT username, COUNT(*) AS item_count
            FROM items
            WHERE created_at = :date
            GROUP BY username
        ) AS counts
        WHERE item_count = (
            SELECT MAX(item_count)
            FROM (
                SELECT username, COUNT(*) AS item_count
                FROM items
                WHERE created_at = :date
                GROUP BY username
            ) AS max_counts
        )
        """, nativeQuery = true)
  List<Map<String, Object>> findUsersByDate(@Param("date") String date);

  @Query(value = """
SELECT i.username FROM items i LEFT JOIN review r
ON r.item_id = i.id AND r.rating = 'poor'
GROUP BY i.username HAVING COUNT(i.id) > 0
AND SUM(CASE WHEN r.id IS NOT NULL THEN 1 ELSE 0 END) = 0
""", nativeQuery =  true)
  List<String> findUsersWhoseItemsNeverGotPoor();

}