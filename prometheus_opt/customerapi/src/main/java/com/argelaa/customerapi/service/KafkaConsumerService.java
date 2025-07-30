package com.argelaa.customerapi.service;

import com.argelaa.common.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService
{
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final CustomerService customerService;

    @Autowired
    public KafkaConsumerService(CustomerService customerService)
    {
        this.customerService = customerService;
    }

    @KafkaListener(topics = "orders", groupId = "my-application-group")
    public void consumeOrderEvent(OrderPlacedEvent event)
    {
        logger.info(String.format("Alınan sipariş olayı: %s", event));

        /* musteri verilerini guncellemek için CustomerService'i cagir */
        customerService.recordPurchase(
                event.getCustomerId(),
                event.getQuantityBought(),
                event.getPricePerUnit()
        );
    }
}
