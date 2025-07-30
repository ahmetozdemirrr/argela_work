package com.argelaa.fstapi.service;

import com.argelaa.common.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService
{
    private static final String TOPIC = "orders";
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderPlacedEvent event)
    {
        logger.info(String.format("Gönderilen sipariş olayı: %s", event));
        this.kafkaTemplate.send(TOPIC, event);
    }
}
