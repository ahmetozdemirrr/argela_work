package com.argelaa.customerapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @Column(name = "total_products_bought")
    private Long totalProductsBought;
    @Column(name = "total_spent_amount")
    private Double totalSpentAmount;

    public Customer()
    {
        this.totalProductsBought = 0L;
        this.totalSpentAmount = 0.0;
    }

    public Customer(Long id, String firstName, String lastName, String email, Long totalProductsBought, Double totalSpentAmount)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.totalProductsBought = totalProductsBought;
        this.totalSpentAmount = totalSpentAmount;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Long getTotalProductsBought()
    {
        return totalProductsBought;
    }

    public void setTotalProductsBought(Long totalProductsBought)
    {
        this.totalProductsBought = totalProductsBought;
    }

    public Double getTotalSpentAmount()
    {
        return totalSpentAmount;
    }

    public void setTotalSpentAmount(Double totalSpentAmount)
    {
        this.totalSpentAmount = totalSpentAmount;
    }

    @Override
    public String toString()
    {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", totalProductsBought=" + totalProductsBought +
                ", totalSpentAmount=" + totalSpentAmount +
                '}';
    }
}
