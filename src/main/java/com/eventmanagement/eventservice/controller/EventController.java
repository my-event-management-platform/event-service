package com.eventmanagement.eventservice.controller;

import com.eventmanagement.eventservice.mapper.EventMapper;
import com.eventmanagement.eventservice.model.Event;
import com.eventmanagement.eventservice.service.EventService;
import com.eventmanagement.shared.dto.request.EventDTO;
import com.eventmanagement.shared.dto.response.EventResponseDTO;
import com.eventmanagement.shared.dto.response.MessageDTO;
import com.eventmanagement.shared.dto.response.PageEventsResponseDTO;
import com.eventmanagement.shared.kafkaEvents.event.EventSubmitted;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

    @GetMapping
    public ResponseEntity<PageEventsResponseDTO> getEvents(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                                           @RequestParam(required = false, defaultValue = "10") @Min(0) int size) {
        PageEventsResponseDTO pageEventsResponseDTO = new PageEventsResponseDTO(); // TBD
        return new ResponseEntity<>(pageEventsResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{event_id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable("event_id") String eventId) {
        Event event = eventService.getReviewedEventById(eventId);
        EventResponseDTO eventResponseDTO = eventMapper.toEventResponseDTO(event);
        return new ResponseEntity<>(eventResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{event_id}")
    public ResponseEntity<MessageDTO> deleteEvent(@PathVariable("event_id") String eventId) {
        return new ResponseEntity<>(new MessageDTO("Event deleted"), HttpStatus.OK);
    }

    @PatchMapping("/{event_id}")
    public ResponseEntity<EventResponseDTO> editEvent(@PathVariable("event_id") String eventId,
                                                      @RequestBody EventDTO eventDTO) {
        return new ResponseEntity<>(new EventResponseDTO(), HttpStatus.OK);
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
