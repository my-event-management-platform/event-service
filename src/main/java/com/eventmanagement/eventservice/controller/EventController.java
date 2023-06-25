package com.eventmanagement.eventservice.controller;

import com.eventmanagement.shared.dto.request.CreateEventDTO;
import com.eventmanagement.shared.dto.response.EventResponseDTO;
import com.eventmanagement.shared.dto.response.MessageDTO;
import com.eventmanagement.shared.dto.response.PageEventsResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@RestController
@RequestMapping("/api/events")
public class EventController {
    @PostMapping
    public ResponseEntity<MessageDTO> createEvent(@Valid @RequestBody CreateEventDTO createEventDTO) {
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
        EventResponseDTO eventResponseDTO = new EventResponseDTO();
        return new ResponseEntity<>(eventResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{event_id}")
    public ResponseEntity<MessageDTO> deleteEvent(@PathVariable("event_id") String eventId) {
        return new ResponseEntity<>(new MessageDTO("Event deleted"), HttpStatus.OK);
    }

    @PatchMapping("/{event_id}")
    public ResponseEntity<EventResponseDTO> editEvent(@PathVariable("event_id") String eventId,
                                                      @RequestBody CreateEventDTO createEventDTO) {
        return new ResponseEntity<>(new EventResponseDTO(), HttpStatus.OK);
    }

}
