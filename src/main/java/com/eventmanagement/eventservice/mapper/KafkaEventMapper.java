package com.eventmanagement.eventservice.mapper;

import com.eventmanagement.eventservice.model.Event;
import com.eventmanagement.shared.kafkaEvents.event.EventChanged;
import com.eventmanagement.shared.kafkaEvents.event.EventPublished;
import com.eventmanagement.shared.kafkaEvents.event.EventSubmitted;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class KafkaEventMapper {
    public abstract EventSubmitted toEventSubmitted(Event event);
    public abstract EventChanged toEventChanged(Event event);
    public abstract EventPublished toEventPublished(Event event);
}
