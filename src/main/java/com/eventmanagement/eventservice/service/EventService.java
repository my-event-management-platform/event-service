package com.eventmanagement.eventservice.service;

import com.eventmanagement.shared.kafkaEvents.KafkaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventService {
    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    public void sendKafkaEvent(KafkaEvent kafkaEvent) {
        Message<KafkaEvent> message = MessageBuilder
                .withPayload(kafkaEvent)
                .setHeader(KafkaHeaders.TOPIC, kafkaEvent.getTopic())
                .build();
        kafkaTemplate.send(message);
    }
}
