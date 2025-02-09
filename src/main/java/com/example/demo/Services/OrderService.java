package com.example.demo.Services;

import com.example.demo.Entities.Contact;
import com.example.demo.Entities.SalesOrderLine;
import com.example.demo.Entities.Order;
import com.example.demo.Repositories.ContactRepository;
import com.example.demo.Repositories.OrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;

    private final ContactRepository contactRepository;

    @Value("${ecommerce.api.url}")
    private String apiUrl;

    @Value("${ecommerce.api.key}")
    private String apiKey;

    public OrderService(RestTemplate restTemplate, OrderRepository orderRepository, ContactRepository contactRepository) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
        this.contactRepository = contactRepository;
    }

    public List<Order> fetchAndSaveOrders() {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/orders")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers), String.class);

            List<Order> orders = parseOrders(response.getBody());
            for (Order order : orders) {
                // Récupérer l'ID du contact
                String contactId = order.getDeliveryToId();  // Récupère l'ID du Contact

                if (contactId != null) {
                    // Chercher le Contact en utilisant l'ID
                    Contact contact = contactRepository.findById(contactId).orElse(null);

                    if (contact != null) {
                        // Si le Contact existe, l'associer à la commande
                        order.setDeliveryTo(contact);
                    } else {
                        // Si le Contact n'est pas trouvé, gérer le cas ici
                        order.setDeliveryTo(null);
                    }
                }

                // Vérifie si la commande existe déjà par son numéro de commande (orderNumber)
                if (!orderRepository.existsByOrderNumber(order.getOrderNumber())) {
                    orderRepository.save(order);
                }
            }

            return orders;  // Retourne la liste des commandes
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }




    private List<Order> parseOrders(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode ordersNode = rootNode.get("results");

            List<Order> orders = new ArrayList<>();

            for (JsonNode orderNode : ordersNode) {
                Order order = objectMapper.treeToValue(orderNode, Order.class);

                // Parse SalesOrderLines
                JsonNode salesOrderLinesNode = orderNode.get("SalesOrderLines").get("results");
                List<SalesOrderLine> salesOrderLines = objectMapper.convertValue(salesOrderLinesNode, new TypeReference<List<SalesOrderLine>>() {
                });

                order.setSalesOrderLines(salesOrderLines);
                orders.add(order);
            }

            return orders;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
