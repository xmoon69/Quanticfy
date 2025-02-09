package com.example.demo.Repositories;

import com.example.demo.Entities.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleOrderLineRepository extends JpaRepository<SalesOrderLine,Long> {
}
