# Makefile for Docker Compose services

# .PHONY: Bu hedeflerin dosya isimleri olmadığını make'e bildirir.
.PHONY: up build clean delete

# Varsayılan hedef: `make` yazıldığında çalışır.
all: up

# Servisleri başlatır ve (gerekirse) imajları yeniden derler.
# --build: Dockerfile'da bir değişiklik varsa imajları yeniden derler.
# --detach (-d): Konteynerleri arka planda başlatır, terminali serbest bırakır.
# Eğer logları terminalde görmek isterseniz -d'yi kaldırabilirsiniz.
up:
	@echo "Starting Docker Compose services..."
	sudo docker compose up

# Sadece servis imajlarını yeniden derler.
build:
	@echo "Building Docker images..."
	sudo docker compose up --build

# Tüm konteynerleri durdurur, kaldırır, volume'leri ve imajları siler.
# --volumes (-v): Tüm isimlendirilmiş volume'leri (MySQL verileri dahil) siler.
# --rmi all: Oluşturulan tüm servis imajlarını siler.
clean:
	@echo "Cleaning up Docker environment..."
	sudo docker compose down --remove-orphans
	@echo "Docker environment cleaned."

delete:
	@echo "Cleaning up Docker environment..."
	sudo docker compose down --volumes --rmi all
	@echo "Docker environment cleaned."
