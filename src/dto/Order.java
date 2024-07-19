package dto;

import java.time.LocalDate;
import java.util.Date;

public class Order {
    private int orderId, totalPrice, payId;
    private String customerId;
    private LocalDate date;
    private String status;

    public Order() {};
    public Order(int orderId, int totalPrice, int payId, String customerId, LocalDate date, String status) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.payId = payId;
        this.customerId = customerId;
        this.date = date;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getPayId() {
        return payId;
    }

    public void setPayId(int payId) {
        this.payId = payId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
