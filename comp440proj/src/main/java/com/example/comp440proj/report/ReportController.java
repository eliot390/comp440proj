package com.example.comp440proj.report;

import com.example.comp440proj.item.ItemRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ItemRepository itemRepo;

    public ReportController(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    // #1
    @GetMapping("/expensive-by-category")
    public List<CatItemView> expensiveByCategory() {
        return itemRepo.mostExpensivePerCategory();
    }

    // #2
    @GetMapping("/users-same-day-two-cats")
    public List<String> usersSameDayTwoCats(@RequestParam String catX,
                                            @RequestParam String catY) {
        return itemRepo.usersWithCatXandCatYSameDay(catX, catY);
    }
}
