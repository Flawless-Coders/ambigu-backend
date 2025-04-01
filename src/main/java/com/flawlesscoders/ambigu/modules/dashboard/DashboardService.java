package com.flawlesscoders.ambigu.modules.dashboard;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flawlesscoders.ambigu.modules.category.Category;
import com.flawlesscoders.ambigu.modules.category.CategoryRepository;
import com.flawlesscoders.ambigu.modules.dish.Dish;
import com.flawlesscoders.ambigu.modules.dish.DishRepository;
import com.flawlesscoders.ambigu.modules.order.Order;
import com.flawlesscoders.ambigu.modules.order.OrderDishes;
import com.flawlesscoders.ambigu.modules.order.OrderRepository;
import com.flawlesscoders.ambigu.modules.user.waiter.WaiterRepository;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final OrderRepository orderRepository;
    private final WaiterRepository waiterRepository;
    private final CategoryRepository categoryRepository;
    private final DishRepository dishRepository;

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

    public ResponseEntity<Map<String, Object>> getHourlySalesByCategory(LocalDate date) {
        try {
            // 1. Configurar rango del día
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            
            // 2. Obtener órdenes del día
            List<Order> orders = orderRepository.findByDateBetween(
                Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant())
            ).stream()
             .filter(order -> !order.isDeleted())
             .toList();
    
            // 3. Obtener categorías únicas
            Set<String> categories = dishRepository.findAll().stream()
                .map(Dish::getCategory)
                .collect(Collectors.toSet());
    
            // 4. Inicializar estructura de datos horarios
            Map<String, Map<String, Double>> hourlyData = new TreeMap<>();
            for (int hour = 0; hour < 24; hour++) {
                String hourKey = String.format("%02d:00", hour);
                hourlyData.put(hourKey, new HashMap<>());
                categories.forEach(cat -> hourlyData.get(hourKey).put(cat, 0.0));
            }
    
            // 5. Procesar cada orden (adaptado a tu estructura con 'dishes')
            for (Order order : orders) {
                int hour = order.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .getHour();
                String hourKey = String.format("%02d:00", hour);
                
                for (OrderDishes dishItem : order.getDishes()) { // Cambiado a getDishes()
                    Dish dish = dishRepository.findById(dishItem.getDishId()).orElse(null);
                    if (dish != null) {
                        double amount = dishItem.getQuantity() * dishItem.getUnitPrice();
                        hourlyData.get(hourKey).merge(
                            dish.getCategory(),
                            amount,
                            Double::sum
                        );
                    }
                }
            }
    
            // 6. Calcular totales por hora
            Map<String, Double> hourlyTotals = new LinkedHashMap<>();
            hourlyData.forEach((hour, categoriesMap) -> {
                double total = categoriesMap.values().stream().mapToDouble(Double::doubleValue).sum();
                hourlyTotals.put(hour, total);
            });

            Map<String, String> categoryNames = categoryRepository.findAll().stream()
            .filter(Category::isStatus)
            .collect(Collectors.toMap(
                Category::getId,
                Category::getName
            ));
    
            // 7. Preparar respuesta
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("date", date.toString());
            response.put("categories", categoryNames);
            response.put("hourlyData", hourlyData);
            response.put("hourlyTotals", hourlyTotals);
    
            return ResponseEntity.ok(response);
    
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error al generar ventas por hora");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Most popular foods of the last 30 days
    public ResponseEntity<Map<String, Object>> getMostPopularFoods() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate start = today.minusDays(30);
            LocalDate end = today;
    
            List<Order> orders = orderRepository.findByDateBetween(
                Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(end.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())
            ).stream()
             .filter(order -> !order.isDeleted())
             .toList();
    
            Map<String, Integer> foodCounts = new HashMap<>();
    
            for (Order order : orders) {
                for (OrderDishes dishItem : order.getDishes()) {
                    String dishId = dishItem.getDishId();
                    if(dishId == null || dishId.isEmpty()) {
                        continue; // Skip if dishId is null or empty
                    }
                    foodCounts.put(dishId, foodCounts.getOrDefault(dishId, 0) + 1);
                }
            }
    
            List<Map.Entry<String, Integer>> sorted = foodCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();
    
            List<Map<String, Object>> topFoods = new ArrayList<>();
            int othersCount = 0;
    
            // Primero, calcula el total de todos los platos
            int totalCount = sorted.stream()
                .mapToInt(Map.Entry::getValue)
                .sum();
    
            // Procesa los 5 principales y suma el resto a othersCount
            for (int i = 0; i < sorted.size(); i++) {
                Map.Entry<String, Integer> entry = sorted.get(i);
                if (i < 5) {
                    Dish dish = dishRepository.findById(entry.getKey()).orElse(null);
                    if (dish != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", dish.getName());
                        map.put("count", entry.getValue());
                        topFoods.add(map);
                    } else {
                        othersCount += entry.getValue();
                    }
                } else {
                    // Todos los elementos después del índice 4 (los 5 primeros) van a "others"
                    othersCount += entry.getValue();
                }
            }
    
            Map<String, Object> response = new HashMap<>();
            response.put("topFoods", topFoods);
            response.put("othersCount", othersCount);
            
            return ResponseEntity.ok(response);
    
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching most popular foods", e);
        }
    }

    //Most popular categories of the last 30 days
    public ResponseEntity<Map<String, Object>> getMostPopularCategories() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusDays(30);
            
            // 1. Obtener órdenes del período
            List<Order> orders = orderRepository.findByDateBetweenAndDeletedFalse(
                Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())
            );
    
            // 2. Extraer todos los dishIds únicos
            Set<String> dishIds = orders.stream()
                .flatMap(order -> order.getDishes().stream())
                .map(OrderDishes::getDishId)
                .filter(dishId -> dishId != null && !dishId.isEmpty())
                .collect(Collectors.toSet());
    
            // 3. Obtener mapeo de platillo a categoría {dishId → categoryId}
            Map<String, String> dishToCategory = dishRepository.findAllById(dishIds).stream()
                .filter(dish -> dish.getCategory() != null)
                .collect(Collectors.toMap(
                    Dish::getId,
                    Dish::getCategory
                ));
    
            // 4. Contar ventas por categoría
            Map<String, Integer> categorySales = new HashMap<>();
            
            orders.forEach(order -> {
                order.getDishes().forEach(dishItem -> {
                    String categoryId = dishToCategory.get(dishItem.getDishId());
                    if (categoryId != null) {
                        categorySales.merge(categoryId, 1, Integer::sum);
                    }
                });
            });
    
            // 5. Obtener nombres de categorías
            Map<String, String> categoryNames = categoryRepository.findAllById(categorySales.keySet()).stream()
                .collect(Collectors.toMap(
                    Category::getId,
                    Category::getName
                ));
    
            // 6. Ordenar por cantidad descendente
            List<Map.Entry<String, Integer>> sortedCategories = categorySales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();
    
            // 7. Preparar respuesta (top 5 + others)
            List<Map<String, Object>> topCategories = new ArrayList<>();
            int othersCount = 0;
    
            for (int i = 0; i < sortedCategories.size(); i++) {
                Map.Entry<String, Integer> entry = sortedCategories.get(i);
                if (i < 5) {
                    Map<String, Object> categoryData = new HashMap<>();
                    categoryData.put("name", categoryNames.get(entry.getKey()));
                    categoryData.put("count", entry.getValue());
                    topCategories.add(categoryData);
                } else {
                    othersCount += entry.getValue();
                }
            }
    
            Map<String, Object> response = new HashMap<>();
            response.put("topCategories", topCategories);
            response.put("othersCount", othersCount);
            
            return ResponseEntity.ok(response);
    
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error al obtener conteo de ventas por categoría", 
                e
            );
        }
    }
}