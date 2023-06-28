package com.eventmanagement.eventservice.mapper;

import com.eventmanagement.eventservice.model.Event;
import com.eventmanagement.shared.dto.request.EventDTO;
import org.mapstruct.*;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class EventMapper {
    public abstract Event toEvent(EventDTO eventDTO);

    @BeforeMapping
    protected void enrichEvent(@MappingTarget Event event) {
        event.setReviewed(false);
    }
}
