package com.eventmanagement.eventservice.service;

import com.eventmanagement.eventservice.mapper.KafkaEventMapper;
import com.eventmanagement.eventservice.model.Event;
import com.eventmanagement.shared.kafkaEvents.KafkaEvent;
import com.eventmanagement.shared.kafkaEvents.event.EventChanged;
import com.eventmanagement.shared.kafkaEvents.event.EventDeleted;
import com.eventmanagement.shared.kafkaEvents.event.EventReviewed;
import com.eventmanagement.shared.kafkaEvents.event.EventSubmitted;
import com.eventmanagement.shared.types.ReviewDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaEventService {
    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;
    private final KafkaEventMapper kafkaEventMapper;
    private final EventService eventService;

    public void processSubmitEvent(Event event) {
        EventSubmitted eventSubmitted = kafkaEventMapper.toEventSubmitted(event);
        sendKafkaEvent(eventSubmitted);
    }

    public void processDeleteEvent(Event event) {
        EventDeleted eventDeleted = new EventDeleted(event.getId());
        sendKafkaEvent(eventDeleted);
    }

    public void processUpdateEvent(Event event) {
        EventChanged eventChanged = kafkaEventMapper.toEventChanged(event);
        sendKafkaEvent(eventChanged);
    }

    private void sendKafkaEvent(KafkaEvent kafkaEvent) {
        Message<KafkaEvent> message = MessageBuilder
                .withPayload(kafkaEvent)
                .setHeader(KafkaHeaders.TOPIC, kafkaEvent.getTopic())
                .build();
        kafkaTemplate.send(message);
    }

    @KafkaListener(topics = "event-reviewed-kafka-events", groupId = "myGroup")
    private void consumeEventReviewed(EventReviewed eventReviewed) {
        if (eventReviewed.getReviewDecision() == ReviewDecision.APPROVE) {
            eventService.markEventAsReviewed(eventReviewed.getEventId());
        } else {
            eventService.deleteEvent(eventReviewed.getEventId(), true);
        }
    }
}