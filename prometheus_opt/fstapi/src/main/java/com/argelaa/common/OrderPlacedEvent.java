package com.argelaa.common;

public class OrderPlacedEvent {
    private Long productId;
    private Long customerId;
    private Long quantityBought; // Alan adı ve tipi 'customer-service' ile uyumlu hale getirildi.
    private double pricePerUnit;
    private String productCategory;

    // JSON serileştirme/deserileştirme için varsayılan constructor
    public OrderPlacedEvent() {
    }

    // Servis içinde kolay nesne oluşturmak için yeni constructor
    public OrderPlacedEvent(Long productId, Long customerId, Long quantityBought, double pricePerUnit,  String productCategory) {
        this.productId = productId;
        this.customerId = customerId;
        this.quantityBought = quantityBought;
        this.pricePerUnit = pricePerUnit;
        this.productCategory = productCategory;
    }

    // Getter ve Setter metotları
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getQuantityBought() {
        return quantityBought;
    }

    public void setQuantityBought(Long quantityBought) {
        this.quantityBought = quantityBought;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public String toString() {
        return "OrderPlacedEvent{" +
                "productId=" + productId +
                ", customerId=" + customerId +
                ", quantityBought=" + quantityBought +
                ", pricePerUnit=" + pricePerUnit +
                ", productCategory='" + productCategory + '\'' +
                '}';
    }
}