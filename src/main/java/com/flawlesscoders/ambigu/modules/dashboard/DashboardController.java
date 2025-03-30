package com.flawlesscoders.ambigu.modules.dashboard;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/top5-waiters")
    public ResponseEntity<List<Map<String, Object>>> getTop5WaitersByRating() {
        return dashboardService.getTop5WaitersByRating();
    }

    @GetMapping("/orders-number/{frame}")
    public ResponseEntity<Map<String, Object>> getOrdersByFrameDay(@PathVariable("frame")String frame) {
        return dashboardService.getOrdersByFrameDay(frame);
    }

    @GetMapping("/orders-percentage")
    public ResponseEntity<Map<String, Object>> getOrdersPercentage(){
        return dashboardService.getOrdersPercentage();
    }

    @GetMapping("/orders-chart")
    public ResponseEntity<Map<String, Object>> getOrdersChart(){
        return dashboardService.buildOrdersChartData();
    }

    @GetMapping("/hourly-category")
    public ResponseEntity<Map<String, Object>> getHourlySalesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dashboardService.getHourlySalesByCategory(date);
    }

    //Most popular foods of the last 30 days
    @GetMapping("/most-popular-foods")
    public ResponseEntity<Map<String, Object>> getMostPopularFoods() {
        return dashboardService.getMostPopularFoods();
    }

    //Most popular categories of the last 30 days
    @GetMapping("/most-popular-categories")
    public ResponseEntity<Map<String, Object>> getMostPopularCategories() {
        return dashboardService.getMostPopularCategories();
    }


}
