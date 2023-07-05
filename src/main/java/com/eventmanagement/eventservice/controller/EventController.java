package com.eventmanagement.eventservice.controller;

import com.eventmanagement.eventservice.mapper.EventMapper;
import com.eventmanagement.eventservice.model.Event;
import com.eventmanagement.eventservice.service.EventService;
import com.eventmanagement.shared.dto.request.EventDTO;
import com.eventmanagement.shared.dto.response.EventResponseDTO;
import com.eventmanagement.shared.dto.response.MessageDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    @PostMapping
    public ResponseEntity<MessageDTO> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        Event event = eventMapper.toEvent(eventDTO);
        eventService.submitEvent(event);
        return new ResponseEntity<>(new MessageDTO("Event submitted for moderation"), HttpStatus.CREATED);
    }

    @GetMapping("/{event_id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable("event_id") String eventId) {
        Event event = eventService.getEventById(eventId, false);
        EventResponseDTO eventResponseDTO = eventMapper.toEventResponseDTO(event);
        return new ResponseEntity<>(eventResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{event_id}")
    public ResponseEntity<MessageDTO> deleteEvent(@PathVariable("event_id") String eventId) {
        eventService.deleteEvent(eventId, false, true);
        return new ResponseEntity<>(new MessageDTO("Event deleted"), HttpStatus.OK);
    }

    @PutMapping("/{event_id}")
    public ResponseEntity<EventResponseDTO> editEvent(@PathVariable("event_id") String eventId,
                                                      @RequestBody EventDTO eventDTO) {
        Event newEvent = eventMapper.toEvent(eventDTO);
        Event modifiedEvent = eventService.updateEvent(eventId, newEvent);
        EventResponseDTO eventResponseDTO = eventMapper.toEventResponseDTO(modifiedEvent);
        return new ResponseEntity<>(eventResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
