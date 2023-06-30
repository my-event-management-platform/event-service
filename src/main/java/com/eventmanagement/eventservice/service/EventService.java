package com.eventmanagement.eventservice.service;

import com.eventmanagement.eventservice.mapper.KafkaEventMapper;
import com.eventmanagement.eventservice.model.Event;
import com.eventmanagement.eventservice.repository.EventRepository;
import com.eventmanagement.eventservice.exception.EventNotFoundException;
import com.eventmanagement.shared.kafkaEvents.KafkaEvent;
import com.eventmanagement.shared.kafkaEvents.event.EventChanged;
import com.eventmanagement.shared.kafkaEvents.event.EventReviewed;
import com.eventmanagement.shared.kafkaEvents.event.EventSubmitted;
import com.eventmanagement.shared.types.ReviewDecision;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventService {
    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;
    private final EventRepository eventRepository;
    private final KafkaEventMapper kafkaEventMapper;

    public void submitEvent(Event event) {
        storeUnreviewedEvent(event);
        EventSubmitted eventSubmitted = kafkaEventMapper.toEventSubmitted(event);
        sendKafkaEvent(eventSubmitted);
    }

    private void sendKafkaEvent(KafkaEvent kafkaEvent) {
        Message<KafkaEvent> message = MessageBuilder
                .withPayload(kafkaEvent)
                .setHeader(KafkaHeaders.TOPIC, kafkaEvent.getTopic())
                .build();
        kafkaTemplate.send(message);
    }

    private void storeUnreviewedEvent(Event event) {
        eventRepository.insert(event);
    }

    public Event getReviewedEventById(String eventId) {
        Event event = eventRepository
                .findByIdAndReviewedIsTrue(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id " + eventId + " is not found"));
        return event;
    }

    public void deleteEvent(String eventId) {
        Event event = getReviewedEventById(eventId);
        eventRepository.deleteById(event.getId());
    }

    private void markEventAsReviewed(String eventId) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id " + eventId + " is not found"));
        event.setReviewed(true);
        eventRepository.save(event);
    }

    public Event updateEvent(String eventId, Event newEvent) {
        Event modifiedEvent = getReviewedEventById(eventId);
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<Event, Event> typeMap = modelMapper.createTypeMap(Event.class, Event.class);
        typeMap.addMappings(mapping -> mapping.skip(Event::setReviewed));
        modelMapper.map(newEvent, modifiedEvent);
        eventRepository.save(modifiedEvent);
        EventChanged eventChanged = kafkaEventMapper.toEventChanged(modifiedEvent);
        sendKafkaEvent(eventChanged);
        return modifiedEvent;
    }

    @KafkaListener(topics = "event-reviewed-kafka-events", groupId = "myGroup")
    private void consumeEventReviewed(EventReviewed eventReviewed) {
        if (eventReviewed.getReviewDecision() == ReviewDecision.APPROVE) {
            markEventAsReviewed(eventReviewed.getEventId());
        } else {
            eventRepository.deleteById(eventReviewed.getEventId());
        }
    }
}
