package com.example.demo.Entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "orders")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @Id
    @JsonProperty("OrderID")
    private String orderId;

    @JsonProperty("OrderNumber")
    private int orderNumber;

    @JsonProperty("DeliverTo")
    private String deliveryToId;

    @JsonProperty("Currency")
    private String currency;





    @JsonProperty("Amount")
    private double amount;

    public Contact getDeliveryTo() {
        return deliveryTo;
    }

    public void setDeliveryTo(Contact deliveryTo) {
        this.deliveryTo = deliveryTo;
    }

    @Transient  // Indique que ce champ n'est pas mappé directement à la base de données
    private Contact deliveryTo;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<SalesOrderLine> salesOrderLines;

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public int getOrderNumber() { return orderNumber; }
    public void setOrderNumber(int orderNumber) { this.orderNumber = orderNumber; }

    public String getDeliveryToId() {
        return deliveryToId;
    }

    public void setDeliveryToId(String deliveryToId) {
        this.deliveryToId = deliveryToId;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }



    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public List<SalesOrderLine> getSalesOrderLines() {
        return salesOrderLines;
    }

    public void setSalesOrderLines(List<SalesOrderLine> salesOrderLines) {
        this.salesOrderLines = salesOrderLines;
    }


}
