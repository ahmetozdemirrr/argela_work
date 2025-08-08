#!/bin/bash

# Customer API'nin çalıştığı adres
BASE_URL="http://localhost:8081/api"
TEST_DURATION_SECONDS=120 # Testin toplam süresi (saniye olarak)

echo "Gelişmiş KPI Test Script'i Başlatılıyor..."
echo "Test Süresi: $TEST_DURATION_SECONDS saniye"
echo "==================================="

# --- SÜREKLİ ŞİKAYET ÇÖZME TESTİ (GRAFİK İÇİN) ---
echo "[CANLI TEST] Şikayet metrikleri 2 dakika boyunca sürekli üretilecek..."

end_time=$((SECONDS + TEST_DURATION_SECONDS))
complaint_id_counter=1

while [ $SECONDS -lt $end_time ]; do
    # 1. Rastgele bir şikayet türü seç
    complaint_types=("Network Issue" "Billing" "Technical" "Sales")
    random_type=${complaint_types[$((RANDOM % ${#complaint_types[@]}))]}

    # 2. Yeni şikayeti oluştur
    echo "  -> Yeni bir '$random_type' şikayeti (ID: $complaint_id_counter) oluşturuluyor..."
    curl -s -X POST "$BASE_URL/complaints" \
         -H "Content-Type: application/json" \
         -d "{
               \"customerId\": $((RANDOM % 5 + 1)),
               \"complaintText\": \"Otomatik test şikayeti.\",
               \"complaintType\": \"$random_type\"
             }" > /dev/null # Çıktıyı gizle

    # Kısa bir süre bekle
    sleep 1

    # 3. Oluşturulan şikayeti çöz
    echo "  -> Şikayet (ID: $complaint_id_counter) çözülüyor..."
    curl -s -X PUT "$BASE_URL/complaints/$complaint_id_counter/resolve" > /dev/null

    complaint_id_counter=$((complaint_id_counter + 1))

    # 2-5 saniye arasında rastgele bekle
    sleep $((RANDOM % 4 + 2))
done

echo ""
echo "==================================="
echo "KPI Test Script'i Tamamlandı."
