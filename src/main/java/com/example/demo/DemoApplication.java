package com.example.demo;

import com.example.demo.Services.ContactService;
import com.example.demo.Services.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private final OrderService orderService;
    private final ContactService contactService;

    public DemoApplication(OrderService orderService, ContactService contactService) {
        this.orderService = orderService;
        this.contactService = contactService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Appel à la méthode fetchAndSaveOrders() pour récupérer et enregistrer les commandes au démarrage
        orderService.fetchAndSaveOrders();

        // Appel à la méthode fetchAndSaveContacts() pour récupérer et enregistrer les contacts au démarrage
        contactService.fetchAndSaveContacts();
    }
}
