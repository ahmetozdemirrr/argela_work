package com.argelaa.fstapi.repository;

import com.argelaa.fstapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository; // JpaRepository'yi import et
import org.springframework.stereotype.Repository; // @Repository'yi import et

import java.util.List;
import java.util.Optional;

@Repository // Spring'in bu interface'i bir Repository bean'i olarak tanımasını sağlar.
public interface ProductRepository extends JpaRepository<Product, Long>
{
    // JpaRepository, ilk parametre olarak üzerinde işlem yapacağı Entity sınıfını (Product),
    // ikinci parametre olarak ise o Entity'nin Primary Key (birincil anahtar) alanının veri tipini (Long) alır.

    // Spring Data JPA, aşağıdaki gibi metod isimlerinden otomatik olarak SQL sorguları türetebilir:
    List<Product> findByCategory(String category); // category alanına göre ürünleri bulan metot
    List<Product> findByQuantityGreaterThan(Long quantity); // quantity alanı belirli bir değerden büyük olan ürünleri bulan metot
    List<Product> findByCategoryAndQuantityGreaterThan(String category, Long quantity); // Hem kategoriye hem de stok miktarına göre bulan metot
    Optional<Product> findByName(String name); // İsimle tam eşleşen ürünü bulan metot
    // Daha karmaşık sorgular için @Query notasyonu kullanılabilir, ancak şimdilik bu kadar yeterli.
}