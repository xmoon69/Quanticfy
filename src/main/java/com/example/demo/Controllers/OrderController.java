package com.example.demo.Controllers;

import com.example.demo.Entities.Order;
import com.example.demo.Entities.SalesOrderLine;
import com.example.demo.Entities.Contact;
import com.example.demo.Services.OrderService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Endpoint pour récupérer et enregistrer les commandes
    @GetMapping("/fetch-orders")
    public ResponseEntity<List<Order>> fetchOrders() {
        List<Order> orders = orderService.fetchAndSaveOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(orders);  // Renvoie les commandes sous forme de JSON
    }

    // Endpoint pour générer le CSV des commandes
    @GetMapping("/flow/orders_to_csv")
    public ResponseEntity<byte[]> getOrdersToCsv() throws IOException {
        List<Order> orders = orderService.fetchAndSaveOrders(); // Récupère les commandes à partir du service
        byte[] csvContent = generateCsvFromOrders(orders);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=orders.csv");
        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }

    // Méthode pour générer le fichier CSV à partir des commandes
    private byte[] generateCsvFromOrders(List<Order> orders) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new java.io.OutputStreamWriter(outputStream), CSVFormat.DEFAULT.withHeader(
                "description", "order", "delivery_name", "delivery_address", "delivery_country", "delivery_zipcode",
                "delivery_city", "items_count", "item_index", "item_id", "item_quantity", "line_price_excl_vat", "line_price_incl_vat"));

        for (Order order : orders) {
            Contact contact = order.getDeliveryTo();  // Récupère le contact associé à la commande

            // Utilisation directe des informations du contact sans vérifier si c'est null
            String deliveryName = contact.getContactName();  // Si contact est null, cela lancera une NullPointerException
            String deliveryAddress = contact.getAddressLine1();
            String deliveryCountry = contact.getCountry();
            String deliveryZipCode = contact.getZipCode();
            String deliveryCity = contact.getCity();

            for (SalesOrderLine salesOrderLine : order.getSalesOrderLines()) {
                csvPrinter.printRecord(
                        salesOrderLine.getDescription(),
                        order.getOrderNumber(),
                        deliveryName,
                        deliveryAddress,
                        deliveryCountry,
                        deliveryZipCode,
                        deliveryCity,
                        order.getSalesOrderLines().size(),
                        salesOrderLine.getId(),
                        salesOrderLine.getItemId(),
                        salesOrderLine.getQuantity(),
                        salesOrderLine.getUnitPrice(),
                        salesOrderLine.getVatAmount()
                );
            }
        }

        csvPrinter.flush();
        return outputStream.toByteArray();
    }
}
