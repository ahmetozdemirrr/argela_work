package com.argelaa.common;

// Kafka üzerinden JSON olarak gönderilecek ve alınacak verinin yapısını temsil eder.
public class OrderPlacedEvent
{
    private Long productId;
    private Long customerId;
    private Long quantityBought;
    private Double pricePerUnit;

    public OrderPlacedEvent()
    {
    }

    public OrderPlacedEvent(Long productId, Long customerId, Long quantityBought, Double pricePerUnit)
    {
        this.productId = productId;
        this.customerId = customerId;
        this.quantityBought = quantityBought;
        this.pricePerUnit = pricePerUnit;
    }

    public Long getProductId()
    {
        return productId;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    public Long getQuantityBought()
    {
        return quantityBought;
    }

    public Double getPricePerUnit()
    {
        return pricePerUnit;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public void setQuantityBought(Long quantityBought)
    {
        this.quantityBought = quantityBought;
    }

    public void setPricePerUnit(Double pricePerUnit)
    {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public String toString()
    {
        return "OrderPlacedEvent{" +
                "productId=" + productId +
                ", customerId=" + customerId +
                ", quantityBought=" + quantityBought +
                ", pricePerUnit=" + pricePerUnit +
                '}';
    }
}
