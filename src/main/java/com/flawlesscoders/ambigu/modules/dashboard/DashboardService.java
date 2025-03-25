package com.flawlesscoders.ambigu.modules.dashboard;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flawlesscoders.ambigu.modules.order.Order;
import com.flawlesscoders.ambigu.modules.order.OrderRepository;
import com.flawlesscoders.ambigu.modules.user.waiter.WaiterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final OrderRepository orderRepository;

    private final WaiterRepository waiterRepository;

    public ResponseEntity<List<Map<String, Object>>> getTop5WaitersByRating() {
        try {
            List<Map<String, Object>> topWaiters = waiterRepository.findTop5ByOrderByAvgRatingDesc().stream().map(waiter -> {
                Map<String, Object> map = new HashMap<>();
                map.put("name", waiter.getName() + " " + waiter.getLastname_p());
                map.put("avgRating", waiter.getAvgRating());
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(topWaiters);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching top waiters", e);
        }
    }
}
