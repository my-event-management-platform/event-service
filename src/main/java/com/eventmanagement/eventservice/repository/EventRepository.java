package com.eventmanagement.eventservice.repository;

import com.eventmanagement.eventservice.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {

}
