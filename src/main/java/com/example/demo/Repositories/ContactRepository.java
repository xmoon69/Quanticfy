package com.example.demo.Repositories;

import com.example.demo.Entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ContactRepository extends JpaRepository<Contact, String> {

}
