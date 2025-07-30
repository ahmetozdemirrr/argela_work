-- Eğer tablo zaten varsa sil
DROP TABLE IF EXISTS customers;

-- Tabloyu oluştur
CREATE TABLE customers (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           first_name VARCHAR(255) NOT NULL,
                           last_name VARCHAR(255) NOT NULL,
                           email VARCHAR(255) UNIQUE NOT NULL,
                           total_products_bought BIGINT DEFAULT 0,
                           total_spent_amount DOUBLE DEFAULT 0.0
);

-- Başlangıç müşteri verileri
INSERT INTO customers (first_name, last_name, email, total_products_bought, total_spent_amount) VALUES
                                                                                                    ('Ahmet', 'Yılmaz', 'ahmet.yilmaz@example.com', 5, 250.50),
                                                                                                    ('Ayşe', 'Demir', 'ayse.demir@example.com', 12, 500.00),
                                                                                                    ('Mehmet', 'Can', 'mehmet.can@example.com', 3, 89.99),
                                                                                                    ('Zeynep', 'Kaya', 'zeynep.kaya@example.com', 20, 1200.75),
                                                                                                    ('Emre', 'Arslan', 'emre.arslan@example.com', 0, 0.00);