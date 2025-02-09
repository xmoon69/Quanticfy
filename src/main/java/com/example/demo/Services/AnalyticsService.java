package com.example.demo.Services;

import com.example.demo.Repositories.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final OrderRepository orderRepository;

    public AnalyticsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Map<String, Object> calculateAnalytics(double quantile) {
        // Étape 1 : Récupérer les chiffres d'affaires par client
        List<Object[]> salesData = orderRepository.getTotalRevenueByCustomer();
        Map<String, Double> customerRevenue = new HashMap<>();

        for (Object[] row : salesData) {
            String customerId = (String) row[0]; // Utiliser String pour le customerId
            Double totalRevenue = (Double) row[1];
            customerRevenue.put(customerId, totalRevenue);
        }

        // Log : 10 clients aléatoires
        List<String> randomCustomers = new ArrayList<>(customerRevenue.keySet());
        Collections.shuffle(randomCustomers);
        randomCustomers.stream().limit(10).forEach(id ->
                System.out.println("Client ID: " + id + ", CA: " + customerRevenue.get(id))
        );

        // Étape 2 : Trier les clients par CA décroissant
        List<Map.Entry<String, Double>> sortedRevenue = customerRevenue.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        // Déterminer le top des clients (quantile)
        int quantileSize = (int) (sortedRevenue.size() * quantile);
        List<Map.Entry<String, Double>> topCustomers = sortedRevenue.subList(0, quantileSize);

        // Étape 3 : Répartition par quantile
        List<Map<String, Object>> quantileStats = new ArrayList<>();
        int segmentSize = sortedRevenue.size() / (int) (1 / quantile);

        for (int i = 0; i < (1 / quantile); i++) {
            int start = i * segmentSize;
            int end = Math.min(start + segmentSize, sortedRevenue.size());

            List<Map.Entry<String, Double>> quantileGroup = sortedRevenue.subList(start, end);
            if (!quantileGroup.isEmpty()) {
                double maxRevenue = quantileGroup.get(0).getValue();
                Map<String, Object> quantileData = new HashMap<>();
                quantileData.put("quantile", (i + 1) * quantile);
                quantileData.put("client_count", quantileGroup.size());
                quantileData.put("max_revenue", maxRevenue);
                quantileStats.add(quantileData);
            }
        }

        // Étape 4 : Retourner les résultats
        Map<String, Object> response = new HashMap<>();
        response.put("top_customers", topCustomers);
        response.put("quantile_stats", quantileStats);
        return response;
    }
}
