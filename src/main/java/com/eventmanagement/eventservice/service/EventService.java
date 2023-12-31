package com.eventmanagement.eventservice.service;

import com.eventmanagement.eventservice.model.Event;
import com.eventmanagement.eventservice.repository.EventRepository;
import com.eventmanagement.shared.exceptions.EventNotFoundException;
import com.eventmanagement.shared.kafkaEvents.event.EventChanged;
import com.eventmanagement.shared.kafkaEvents.event.EventDeleted;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventService {
    private final EventRepository eventRepository;
    private final KafkaEventService kafkaEventService;

    public void submitEvent(Event event) {
        storeUnreviewedEvent(event);
        kafkaEventService.processSubmitEvent(event);
    }

    @Transactional
    protected void storeUnreviewedEvent(Event event) {
        eventRepository.insert(event);
    }

    @Transactional(readOnly = true)
    public Event getEventById(String eventId, boolean allowUnreviewed) {
        Event event;
        if (allowUnreviewed) {
            event = eventRepository
                    .findById(eventId)
                    .orElseThrow(() -> new EventNotFoundException("Event with id " + eventId + " is not found"));
        } else {
            event = eventRepository
                    .findByIdAndReviewedIsTrue(eventId)
                    .orElseThrow(() -> new EventNotFoundException("Event with id " + eventId + " is not found"));
        }
        return event;
    }

    @Transactional
    public void deleteEvent(String eventId, boolean allowUnreviewed, boolean produceKafkaEvent) {
        Event event = getEventById(eventId, allowUnreviewed);
        eventRepository.deleteById(event.getId());
        kafkaEventService.processDeleteEvent(event);
    }

    @Transactional
    public void markEventAsReviewed(String eventId, boolean produceKafkaEvent) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id " + eventId + " is not found"));
        event.setReviewed(true);
        eventRepository.save(event);
        if (produceKafkaEvent) {
            kafkaEventService.processPublishEvent(event);
        }
    }

    @Transactional
    public Event updateEvent(String eventId, Event newEvent) {
        Event modifiedEvent = getEventById(eventId, false);
        // Define mapper
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true); // Skip null
        modelMapper.map(newEvent, modifiedEvent);
        // Save modified event data
        eventRepository.save(modifiedEvent);
        kafkaEventService.processUpdateEvent(modifiedEvent);
        return modifiedEvent;
    }

}
