package com.argelaa.common; // veya senin belirlediğin ortak paket

public class OrderPlacedEvent {
    private Long productId;
    private Long customerId;
    private int quantity;
    private double pricePerUnit; // Bu alanı da ekleyebilirsin

    // JSON dönüşümü için boş constructor GEREKLİ
    public OrderPlacedEvent() {
    }

    // Getter ve Setter'lar
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }
}