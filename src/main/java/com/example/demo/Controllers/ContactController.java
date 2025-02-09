package com.example.demo.Controllers;

import com.example.demo.Services.ContactService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }


    @GetMapping("/fetch-contacts")
    public ResponseEntity<String> fetchContacts() {
        contactService.fetchAndSaveContacts();
        return ResponseEntity.ok("Contacts fetched and saved successfully");
    }
}



