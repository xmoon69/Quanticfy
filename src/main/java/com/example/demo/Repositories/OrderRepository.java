package com.example.demo.Repositories;

import com.example.demo.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository <Order,String>{
    boolean existsByOrderNumber(Integer orderNumber);

    @Query("SELECT o.deliveryToId, SUM(soi.amount) " +
            "FROM Order o JOIN o.salesOrderLines soi " +
            "GROUP BY o.deliveryToId")
    List<Object[]> getTotalRevenueByCustomer();
}
