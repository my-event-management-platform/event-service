package com.eventmanagement.eventservice.repository;

import com.eventmanagement.eventservice.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface EventRepository extends MongoRepository<Event, String> {
    Optional<Event> findByIdAndReviewedIsTrue(String id);
}
