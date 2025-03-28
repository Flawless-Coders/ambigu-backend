package com.flawlesscoders.ambigu.modules.dashboard;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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

    public ResponseEntity<Map<String, Object>> getOrdersByFrameDay(String frame) {
        try {
            Date from = new Date();
            Date to = new Date();
            boolean isRange = false;
    
            //Determinate Date Range
            switch (frame) {
                case "today":
                    from = getStartOfDay(new Date());
                    to = getEndOfDay(new Date());
                    break;
                case "week":
                    from = new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);
                    to = getEndOfDay(new Date());
                    isRange = true;
                    break;
                case "month":
                    from = new Date(System.currentTimeMillis() - 29L * 24 * 60 * 60 * 1000);
                    to = getEndOfDay(new Date());
                    isRange = true;
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid frame value");
            }
    
            // Find range on db
            List<Order> orders = orderRepository.findByDateBetween(from, to);
    
            if (isRange) {
                // Count orders per day
                Map<String, Long> dailyOrdersMap = new HashMap<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
                for (Order order : orders) {
                    String date = dateFormat.format(order.getDate());
                    dailyOrdersMap.put(date, dailyOrdersMap.getOrDefault(date, 0L) + 1);
                }
    
                // Fill with 0 the missing days
                List<Map<String, Object>> filledDailyOrders = new ArrayList<>();
                List<String> allDates = getAllDatesInRange(from, to);
                for (String date : allDates) {
                    filledDailyOrders.add(Map.of(
                        "date", date,
                        "count", dailyOrdersMap.getOrDefault(date, 0L)
                    ));
                }
    
                // Calcule orders total
                long totalOrders = orders.size();
    
                // CBuild response
                Map<String, Object> response = new HashMap<>();
                response.put("totalOrders", totalOrders);
                response.put("dailyOrders", filledDailyOrders);
    
                return ResponseEntity.ok(response);
            } else {
                // Hour process
                Map<Integer, Long> hourlyOrdersMap = new HashMap<>();
                Calendar calendar = Calendar.getInstance();
    
                for (Order order : orders) {
                    calendar.setTime(order.getDate());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    hourlyOrdersMap.put(hour, hourlyOrdersMap.getOrDefault(hour, 0L) + 1);
                }
    
                // Fill with 0
                List<Map<String, Object>> filledHourlyOrders = new ArrayList<>();
                for (int hour = 0; hour < 24; hour++) {
                    filledHourlyOrders.add(Map.of(
                        "hour", hour,
                        "count", hourlyOrdersMap.getOrDefault(hour, 0L)
                    ));
                }
    
                // Calculate total orders
                long totalOrders = orders.size();
    
                // Build response
                Map<String, Object> response = new HashMap<>();
                response.put("totalOrders", totalOrders);
                response.put("hourlyOrders", filledHourlyOrders);
    
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching orders", e);
        }
    }
    
    private List<String> getAllDatesInRange(Date from, Date to) {
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
    
        while (!calendar.getTime().after(to)) {
            Date currentDate = calendar.getTime();
            dates.add(new SimpleDateFormat("yyyy-MM-dd").format(currentDate));
            calendar.add(Calendar.DATE, 1);
        }
    
        return dates;
    }
    
    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    //Get the percentage of increase or decrease of the orders from the last day
    public ResponseEntity<Map<String, Object>> getOrdersPercentage() {
        try {
            Date today = new Date();
            Date yesterday = new Date(System.currentTimeMillis() - 1L * 24 * 60 * 60 * 1000);
    
            long todayOrders = orderRepository.countByDate(today);
            long yesterdayOrders = orderRepository.countByDate(yesterday);
    
            double percentage = 0;
            if (yesterdayOrders != 0) {
                percentage = ((double) todayOrders - yesterdayOrders) / yesterdayOrders * 100;
            }
    
            Map<String, Object> response = new HashMap<>();
            response.put("percentage", percentage);
    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching orders percentage", e);
        }
    }

    public ResponseEntity<Map<String, Object>> buildOrdersChartData(){
        try{
            LocalDate today = LocalDate.now();
            LocalDate end = today;
            LocalDate start = end.minusDays(29);

            //Last period
            LocalDate previousEnd = start.minusDays(1);
            LocalDate previousStart = previousEnd.minusDays(29);

            Map<String, Integer> currentMap = getCountsWithRepository(start, end);
            Map<String, Integer> previousMap = getCountsWithRepository(previousStart, previousEnd);

            int total = currentMap.values().stream().mapToInt(Integer::intValue).sum();
            int average = (int) currentMap.values().stream().mapToInt(Integer::intValue).average().orElse(0);
            int previousAverage = (int) previousMap.values().stream().mapToInt(Integer::intValue).average().orElse(1); // evitar división por 0
            double growth;
            if (previousAverage == 0) {
                growth = average > 0 ? 100.0 : 0.0;
            } else {
                growth = ((double) (average - previousAverage) / previousAverage) * 100;
            }

            List<String> labels = new ArrayList<>(currentMap.keySet());
            List<Integer> dailyOrders = new ArrayList<>(currentMap.values());

            Map<String, Object> response = new HashMap<>();
            response.put("total", total);
            response.put("average", average);
            response.put("previousAverage", previousAverage);
            response.put("growth", growth);
            response.put("labels", labels);
            response.put("dailyOrders", dailyOrders);
            response.put("fromDate", start.toString()); // yyyy-MM-dd
            response.put("toDate", end.toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error al generar datos del gráfico");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private Map<String, Integer> getCountsWithRepository(LocalDate from, LocalDate to) {
        Date fromDate = java.sql.Date.valueOf(from);
        Date toDate = java.sql.Date.valueOf(to);

        // Obtener todas las órdenes activas en el rango
        List<Order> orders = orderRepository.findByDateBetween(fromDate, toDate).stream()
                .filter(order -> !order.isDeleted())
                .toList();

        // Inicializar mapa con todos los días del rango (inicializados en 0)
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            String label = capitalize(date.format(DateTimeFormatter.ofPattern("MMM d", new Locale("es"))));
            counts.put(label, 0);
        }

        // Agrupar en memoria
        for (Order order : orders) {
            LocalDate date = order.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String label = capitalize(date.format(DateTimeFormatter.ofPattern("MMM d", new Locale("es"))));
            counts.computeIfPresent(label, (k, v) -> v + 1);
        }

        return counts;
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}