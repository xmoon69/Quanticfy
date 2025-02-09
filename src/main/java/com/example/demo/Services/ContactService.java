package com.example.demo.Services;

import com.example.demo.Entities.Contact;
import com.example.demo.Repositories.ContactRepository;
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
public class ContactService {

    private final RestTemplate restTemplate;
    private final ContactRepository contactRepository;

    @Value("${ecommerce.api.url}")
    private String apiUrl;

    @Value("${ecommerce.api.key}")
    private String apiKey;

    public ContactService(RestTemplate restTemplate, ContactRepository contactRepository) {
        this.restTemplate = restTemplate;
        this.contactRepository = contactRepository;
    }

    public void fetchAndSaveContacts() {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/contacts")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers), String.class);

            List<Contact> contacts = parseContacts(response.getBody());
            for (Contact contact : contacts) {
                contactRepository.save(contact);
            }

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    private List<Contact> parseContacts(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode contactsNode = rootNode.get("results");

            List<Contact> contacts = new ArrayList<>();
            for (JsonNode contactNode : contactsNode) {
                Contact contact = objectMapper.treeToValue(contactNode, Contact.class);
                contacts.add(contact);
            }

            return contacts;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
