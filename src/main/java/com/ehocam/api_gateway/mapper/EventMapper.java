package com.ehocam.api_gateway.mapper;

import com.ehocam.api_gateway.dto.EventDto;
import com.ehocam.api_gateway.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    Event toEntity(EventDto.Create dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "engagement", ignore = true)
    void updateEntity(EventDto.Update dto, @MappingTarget Event entity);

    EventDto.Response toResponseDto(Event entity);

    EventDto.MediaDto toMediaDto(Event.Media media);
    Event.Media toMediaEntity(EventDto.MediaDto dto);

    EventDto.EngagementDto toEngagementDto(Event.Engagement engagement);
    Event.Engagement toEngagementEntity(EventDto.EngagementDto dto);

    EventDto.I18nContentDto toI18nContentDto(Event.I18nContent content);
    Event.I18nContent toI18nContentEntity(EventDto.I18nContentDto dto);
}
