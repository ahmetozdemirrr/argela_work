# Makefile for Docker Compose services

.PHONY: up build clean delete

all: up

up:
	@echo "Starting Docker Compose services..."
	sudo docker compose up

build:
	@echo "Building Docker images..."
	sudo docker compose up --build

clean:
	@echo "Cleaning up Docker environment..."
	sudo docker compose down --remove-orphans
	@echo "Docker environment cleaned."

delete:
	@echo "Deleting Docker images..."
	sudo docker compose down --volumes --rmi all
	@echo "Docker environment cleaned."
