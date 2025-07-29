package com.argelaa.fstapi.model;

import jakarta.persistence.*;

@Entity /* Bu notasyon, Product sınıfının bir JPA varlığı olduğunu ve bir veritabanı tablosuna eşleneceğini belirtir. */
@Table(name = "products") /* Bu varlığın veritabanındaki "products" adlı tabloya eşlendiğini belirtir. */
public class Product
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long         id;
	private String     name;
	private Double    price;
	private Long   quantity;
	private String category;

	public Product() { /* constructor 1 */

	}

	public Product(Long id, String name, Double price, Long quantity, String category)
	{
		this.id = id;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.category = category;
	}

	public Long getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public Double getPrice()
	{
		return price;
	}

	public Long getQuantity()
	{
		return quantity;
	}

	public String getCategory()
	{
		return category;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setPrice(Double price)
	{
		this.price = price;
	}

	public void setQuantity(Long quantity)
	{
		this.quantity = quantity;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}
}
